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
	public void authenticated() {
		logger.info( "Successful authenticated." );
	}

	@Override
	public void authenticationFailed( final Throwable reason ) {
		logger.error( "Authentication failed: ", reason );
	}

}
