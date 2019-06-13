package de.retest.recheck.auth;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserAuthenticationHandler implements AuthenticationHandler {

	private static final Logger logger = LoggerFactory.getLogger( BrowserAuthenticationHandler.class );

	@Override
	public void showWebLoginUri( final URI loginUri ) {
		try {
			Desktop.getDesktop().browse( loginUri );
		} catch ( final IOException e ) {
			logger.error( "Error opening browser for '{}'.", loginUri );
		}
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
