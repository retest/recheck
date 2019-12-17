package de.retest.recheck.auth;

import static org.keycloak.adapters.rotation.AdapterTokenVerifier.verifyToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.OAuthErrorException;
import org.keycloak.adapters.ServerRequest.HttpFailure;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.omg.CORBA.ServerRequest;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetestAuthentication {

	private static final String REALM = "customer";
	private static final String URL = "https://sso.prod.cloud.retest.org/auth";
	private static final String BASE_URL = URL + "/realms/" + REALM + "/protocol/openid-connect";
	private static final String CERTS_URL = BASE_URL + "/certs";

	private static final String KID = "cXdlj_AlGVf-TbXyauXYM2XairgNUahzgOXHAuAxAmQ";

	private String accessToken;
	private final AuthenticationHandler handler;
	private final String client;
	private final JWTVerifier verifier;

	public RetestAuthentication( final AuthenticationHandler handler, final String client ) {
		this.handler = handler;
		this.client = client;
		verifier = getJwtVerifier();
	}

	private JWTVerifier getJwtVerifier() {
		try {
			final UrlJwkProvider provider = new UrlJwkProvider( URI.create( CERTS_URL ).toURL() );
			final PublicKey publicKey = provider.get( KID ).getPublicKey();
			return JWT.require( Algorithm.RSA256( (RSAPublicKey) publicKey, null ) ).build();
		} catch ( final JwkException | MalformedURLException e ) {
			throw new RuntimeException( "Error accessing keycloak JWK information", e );
		}
	}

	public void authenticate() {
		if ( handler.getOfflineToken() != null ) {
			try {
				refreshTokens();
			} catch ( IOException | HttpFailure e ) {
				log.info( "Token not recognized, initiating authentication" );
				login();
			}
		} else {
			log.info( "No active token found, initiating authentication" );
			login();
		}
	}

	private void login() {
		try {
			final CallbackListener callback = new CallbackListener();
			callback.start();

			final String redirectUri = "http://localhost:" + callback.server.getLocalPort();
			final String state = UUID.randomUUID().toString();

			final KeycloakUriBuilder builder = deployment.getAuthUrl().clone() //
					.queryParam( OAuth2Constants.RESPONSE_TYPE, OAuth2Constants.CODE ) //
					.queryParam( OAuth2Constants.CLIENT_ID, deployment.getResourceName() ) //
					.queryParam( OAuth2Constants.REDIRECT_URI, redirectUri ) //
					.queryParam( OAuth2Constants.STATE, state ) //
					.queryParam( OAuth2Constants.SCOPE, OAuth2Constants.OFFLINE_ACCESS );

			final URI loginUri = URI.create( builder.build().toString() );
			handler.showWebLoginUri( loginUri );

			callback.join();

			if ( !state.equals( callback.result.getState() ) ) {
				final VerificationException reason = new VerificationException( "Invalid state" );
				handler.loginFailed( reason );
			}

			if ( callback.result.getError() != null ) {
				final OAuthErrorException reason =
						new OAuthErrorException( callback.result.getError(), callback.result.getErrorDescription() );
				handler.loginFailed( reason );
			}

			if ( callback.result.getErrorException() != null ) {
				handler.loginFailed( callback.result.getErrorException() );
			}

			final AccessTokenResponse tokenResponse =
					ServerRequest.invokeAccessCodeToToken( deployment, callback.result.getCode(), redirectUri, null );
			accessToken = tokenResponse.getToken();

			handler.loginPerformed( tokenResponse.getRefreshToken() );
		} catch ( final InterruptedException | IOException | HttpFailure e ) {
			log.error( "Error during authentication", e );
			Thread.currentThread().interrupt();
		}

	}

	public void logout() {
		final String offlineToken = handler.getOfflineToken();
		if ( offlineToken != null ) {
			try {
				log.info( "Performing logout" );
				ServerRequest.invokeLogout( deployment, offlineToken );
				handler.logoutPerformed();
			} catch ( IOException | HttpFailure e ) {
				log.error( "Error during logout", e );
				handler.logoutFailed( e );
			}
		} else {
			log.error( "No offline token provided" );
		}
	}

	public String getAccessToken() {
		try {
			refreshTokens();
		} catch ( IOException | HttpFailure e ) {
			log.error( "Error refreshing token(s)", e );
		}
		return accessToken;
	}

	private void refreshTokens() throws IOException, HttpFailure {
		if ( !isTokenValid() ) {
			final AccessTokenResponse response = ServerRequest.invokeRefresh( deployment, handler.getOfflineToken() );
			accessToken = response.getToken();
		}
	}

	private boolean isTokenValid() {
		try {
			return accessToken != null && verifyToken( accessToken, deployment ).isActive();
		} catch ( final VerificationException e ) {
			log.info( "Current token is invalid, requesting new one" );
		}
		return false;
	}

	static KeycloakResult getRequestParameters( final String request ) {
		final String url = "http://localhost/" + request.split( " " )[1];
		final Map<String, String> parameters = URLEncodedUtils.parse( URI.create( url ), StandardCharsets.UTF_8 ) //
				.stream() //
				.collect( Collectors.toMap( NameValuePair::getName, NameValuePair::getValue ) );

		return KeycloakResult.builder() //
				.code( parameters.get( OAuth2Constants.CODE ) ) //
				.error( parameters.get( OAuth2Constants.ERROR ) ) //
				.errorDescription( parameters.get( "error-description" ) ) //
				.state( parameters.get( OAuth2Constants.STATE ) ) //
				.build();
	}

	private class CallbackListener extends Thread {

		private final ServerSocket server;
		private KeycloakResult result;

		public CallbackListener() throws IOException {
			server = new ServerSocket( 0 );
		}

		@Override
		public void run() {
			try ( Socket socket = server.accept() ) {
				@Cleanup
				final BufferedReader br = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
				final String request = br.readLine();

				result = getRequestParameters( request );

				@Cleanup
				final OutputStreamWriter out = new OutputStreamWriter( socket.getOutputStream() );
				@Cleanup
				final PrintWriter writer = new PrintWriter( out );

				if ( result.getError() == null ) {
					writer.println( "HTTP/1.1 302 Found" );
					writer.println( "Location: " + deployment.getTokenUrl().replace( "/token", "/delegated" ) );

				} else {
					writer.println( "HTTP/1.1 302 Found" );
					writer.println(
							"Location: " + deployment.getTokenUrl().replace( "/token", "/delegated?error=true" ) );

				}
			} catch ( final IOException e ) {
				log.error( "Error during communication with sso.cloud.retest.org", e );
			}
		}

	}

}
