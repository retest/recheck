package de.retest.recheck.auth;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthenticationHandler implements AuthenticationHandler {

	private static final Logger logger = LoggerFactory.getLogger( DefaultAuthenticationHandler.class );

	@Override
	public void showWebLoginUri( final URI loginUri ) {
		logger.info( "Log in using '{}'.", loginUri );
	}

	@Override
	public void loginPerformed( final String token ) {
		logger.info( "Login successful." );
	}

	@Override
	public void loginFailed( final Throwable reason ) {
		logger.error( "Login failed: ", reason );
	}

	@Override
	public String getOfflineToken() {
		return null;
	}

	@Override
	public void logoutPerformed() {
		logger.info( "Logout successful." );
	}

	@Override
	public void logoutFailed( final Throwable reason ) {
		logger.error( "Logout failed:", reason );
	}

}
