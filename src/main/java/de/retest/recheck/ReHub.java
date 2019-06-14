package de.retest.recheck;

import java.util.prefs.Preferences;

import de.retest.recheck.auth.ReHubAuthenticationHandler;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.CloudPersistence;

public class ReHub {

	private ReHub() {

	}

	public static void init() {
		System.setProperty( Properties.REPORT_OUTPUT_FORMAT_PROPERTY, Properties.FileOutputFormat.CLOUD.toString() );

		final RetestAuthentication auth = RetestAuthentication.getInstance();

		if ( !auth.isAuthenticated( getToken() ) ) {
			auth.login( new ReHubAuthenticationHandler() );
		}
	}

	private static String getToken() {
		final String tokenFromEnvironment = System.getenv( CloudPersistence.RECHECK_API_KEY );
		final String tokenFromPreferences =
				Preferences.userNodeForPackage( ReHub.class ).get( CloudPersistence.RECHECK_API_KEY, null );

		return tokenFromEnvironment != null ? tokenFromEnvironment : tokenFromPreferences;
	}

}
