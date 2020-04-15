package de.retest.recheck.auth;

public class UnableToAuthenticateOfflineException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnableToAuthenticateOfflineException( final Throwable cause ) {
		super( "It appears you are offline. You need either to be online or have a special offline-license.", cause );
	}

}
