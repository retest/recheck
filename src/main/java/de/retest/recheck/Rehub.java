package de.retest.recheck;

import java.util.prefs.Preferences;

import de.retest.recheck.Properties.FileOutputFormat;
import de.retest.recheck.auth.RehubAuthenticationHandler;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.CloudPersistence;

public class Rehub {

	private Rehub() {

	}

	public static void init() {
		final RetestAuthentication auth = RetestAuthentication.getInstance();

		if ( !auth.isAuthenticated( getToken() ) ) {
			auth.login( new RehubAuthenticationHandler() );
		}
		System.setProperty( Properties.FILE_OUTPUT_FORMAT_PROPERTY, FileOutputFormat.CLOUD.toString() );
	}

	private static String getToken() {
		final String tokenFromEnvironment = System.getenv( CloudPersistence.RECHECK_API_KEY );
		final String tokenFromPreferences =
				Preferences.userNodeForPackage( Rehub.class ).get( CloudPersistence.RECHECK_API_KEY, null );

		return tokenFromEnvironment != null ? tokenFromEnvironment : tokenFromPreferences;
	}

}
