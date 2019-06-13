package de.retest.recheck;

import java.util.prefs.Preferences;

import de.retest.recheck.auth.BrowserAuthenticationHandler;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.CloudPersistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteRecheckImpl extends RecheckImpl {

	private final Preferences preference = Preferences.userNodeForPackage( getClass() );

	public RemoteRecheckImpl() {
		this( RecheckOptions.builder().build() );
	}

	public RemoteRecheckImpl( final RecheckOptions options ) {
		super( options );
		System.setProperty( Properties.REPORT_OUTPUT_FORMAT_PROPERTY, Properties.FileOutputFormat.CLOUD.toString() );

		final RetestAuthentication auth = RetestAuthentication.getInstance();

		final String token = getToken();

		if ( !auth.isAuthenticated( token ) ) {
			auth.login( new BrowserAuthenticationHandler() );
		}

		if ( token == null ) {
			final String refreshTokenString = auth.getRefreshTokenString();
			log.info( "Please use this token for your CI: {}", refreshTokenString );
			preference.put( CloudPersistence.RECHECK_API_KEY, refreshTokenString );
		}

	}

	private String getToken() {
		final String tokenFromEnvironment = System.getenv( CloudPersistence.RECHECK_API_KEY );
		final String tokenFromPreferences = preference.get( CloudPersistence.RECHECK_API_KEY, null );

		return tokenFromEnvironment != null ? tokenFromEnvironment : tokenFromPreferences;
	}

}
