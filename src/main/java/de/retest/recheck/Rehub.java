package de.retest.recheck;

import java.util.prefs.Preferences;

import de.retest.recheck.auth.RehubAuthenticationHandler;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.CloudPersistence;
import de.retest.recheck.persistence.FileOutputFormat;

public class Rehub {

	private Rehub() {

	}

	/**
	 * Initializes rehub to be used as persistence and, if not already authenticated, asks for login.
	 */
	public static void init() {
		System.setProperty( RetestAuthentication.RESOURCE_PROPERTY, "marvin" );

		final RetestAuthentication auth = RetestAuthentication.getInstance();
		auth.authenticate( new RehubAuthenticationHandler() );
		RecheckProperties.getInstance().setProperty( "de.retest.output.Format", FileOutputFormat.CLOUD.toString() );
	}

	/**
	 * Returns the given recheck API key for rehub.
	 *
	 * <p>
	 * <em>Treat this as a secret! Anyone with access to your token can add test reports to rehub.</em>
	 *
	 * @return The given recheck API key for rehub.
	 */
	public static String getRecheckApiKey() {
		final String tokenFromEnvironment = System.getenv( CloudPersistence.RECHECK_API_KEY );
		final String tokenFromPreferences =
				Preferences.userNodeForPackage( Rehub.class ).get( CloudPersistence.RECHECK_API_KEY, null );

		return tokenFromEnvironment != null ? tokenFromEnvironment : tokenFromPreferences;
	}

}
