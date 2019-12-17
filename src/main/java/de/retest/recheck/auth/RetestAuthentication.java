package de.retest.recheck.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.keycloak.OAuth2Constants;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetestAuthentication {

	private static final String REALM = "customer";
	private static final String URL = "https://sso.prod.cloud.retest.org/auth";
	private static final String BASE_URL = URL + "/realms/" + REALM + "/protocol/openid-connect";
	private static final String AUTH_URL = BASE_URL + "/auth";
	private static final String TOKEN_URL = BASE_URL + "/token";
	private static final String CERTS_URL = BASE_URL + "/certs";
	private static final String LOGOUT_URL = BASE_URL + "/logout";

	private static final String KID = "cXdlj_AlGVf-TbXyauXYM2XairgNUahzgOXHAuAxAmQ";

	private DecodedJWT accessToken;
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
			refreshTokens();
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

			final URIBuilder builder = new URIBuilder( AUTH_URL );
			builder.addParameter( "response_type", "code" );
			builder.addParameter( "client_id", client );
			builder.addParameter( "redirect_uri", redirectUri );
			builder.addParameter( "state", state );
			builder.addParameter( "scope", "offline_access" );

			final URI loginUri = URI.create( builder.build().toString() );
			handler.showWebLoginUri( loginUri );

			callback.join();

			if ( !state.equals( callback.result.getState() ) ) {
				handler.loginFailed( new RuntimeException() );
			}

			if ( callback.result.getError() != null ) {
				handler.loginFailed( new RuntimeException() );
			}

			if ( callback.result.getErrorException() != null ) {
				handler.loginFailed( callback.result.getErrorException() );
			}

			final TokenBundle bundle = accessCodeToToken( callback.result.getCode(), redirectUri );
			accessToken = verifier.verify( bundle.accessToken );

			handler.loginPerformed( bundle.refreshToken );
		} catch ( final InterruptedException | IOException | URISyntaxException e ) {
			log.error( "Error during authentication", e );
			Thread.currentThread().interrupt();
		}

	}

	private TokenBundle accessCodeToToken( final String code, final String redirectUri ) {
		final TokenBundle bundle = new TokenBundle();

		final HttpResponse<JsonNode> response = Unirest.post( TOKEN_URL ) //
				.field( "grant_type", "authorization_code" ) //
				.field( "code", code ) //
				.field( "client_id", client ) //
				.field( "redirect_uri", redirectUri ) //
				.asJson();

		if ( response.isSuccess() ) {
			final JSONObject object = response.getBody().getObject();
			bundle.setAccessToken( object.getString( "access_token" ) );
			bundle.setRefreshToken( object.getString( "refresh_token" ) );
		}

		return bundle;
	}

	@Data
	private static class TokenBundle {
		private String accessToken;
		private String refreshToken;
	}

	public void logout() {
		final String offlineToken = handler.getOfflineToken();
		if ( offlineToken != null ) {
			final HttpResponse<JsonNode> response = Unirest.post( LOGOUT_URL ) //
					.field( "refresh_token", handler.getOfflineToken() ) //
					.field( "client_id", client ) //
					.asJson();

			if ( response.isSuccess() ) {
				handler.logoutPerformed();
			} else {
				handler.logoutFailed( new RuntimeException( response.getStatusText() ) );
			}

		} else {
			log.error( "No offline token provided" );
		}
	}

	private void refreshTokens() {
		final Optional<DecodedJWT> refreshedToken = refreshAccessToken();
		if ( refreshedToken.isPresent() ) {
			accessToken = refreshedToken.get();
		} else {
			login();
		}
	}

	public DecodedJWT getAccessToken() {
		if ( !isAccessTokenValid() ) {
			refreshTokens();
		}
		return accessToken;
	}

	private Optional<DecodedJWT> refreshAccessToken() {
		final HttpResponse<JsonNode> response = Unirest.post( TOKEN_URL ) //
				.field( "grant_type", "refresh_token" ) //
				.field( "refresh_token", handler.getOfflineToken() ) //
				.field( "client_id", client ) //
				.asJson();

		if ( response.isSuccess() ) {
			final JSONObject object = response.getBody().getObject();
			return Optional.of( verifier.verify( object.getString( "access_token" ) ) );
		} else {
			log.error( "Error retrieving access token: {}", response.getStatusText() );
			return Optional.empty();
		}
	}

	private boolean isAccessTokenValid() {
		try {
			final DecodedJWT verify = verifier.verify( accessToken );
			return accessToken != null && verify != null;
		} catch ( final JWTVerificationException exception ) {
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
