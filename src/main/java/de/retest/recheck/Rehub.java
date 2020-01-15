package de.retest.recheck;

import static de.retest.recheck.RecheckProperties.FILE_OUTPUT_FORMAT_PROPERTY_KEY;

import de.retest.recheck.auth.RehubAuthenticationHandler;
import de.retest.recheck.auth.RetestAuthentication;
import de.retest.recheck.persistence.FileOutputFormat;

public class Rehub {

	private static final String REHUB_CLIENT = "marvin";

	private static RehubAuthenticationHandler handler;
	private static RetestAuthentication auth;

	private Rehub() {

	}

	/**
	 * Initializes rehub authentication and the cloud persistence.
	 */
	public static void init() {
		handler = new RehubAuthenticationHandler();
		auth = new RetestAuthentication( handler, REHUB_CLIENT );
		System.setProperty( FILE_OUTPUT_FORMAT_PROPERTY_KEY, FileOutputFormat.CLOUD.toString() );
	}

	/**
	 * Initializes rehub (if needed) and perform login (if needed).
	 */
	public static void authenticate() {
		if ( auth == null ) {
			init();
		}
		auth.authenticate();
	}

	public static void logout() {
		if ( auth != null ) {
			auth.logout();
		}
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
		return handler.getOfflineToken();
	}

	public static String getAccessToken() {
		return auth.getAccessToken().getToken();
	}

}
