package de.retest.recheck.auth;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.Rehub;
import de.retest.recheck.persistence.CloudPersistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReHubAuthenticationHandler implements AuthenticationHandler {

	private static final Logger logger = LoggerFactory.getLogger( ReHubAuthenticationHandler.class );

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
		final String refreshTokenString = RetestAuthentication.getInstance().getRefreshTokenString();
		log.info( "Please use this token for your CI: {}", refreshTokenString );
		Preferences.userNodeForPackage( Rehub.class ).put( CloudPersistence.RECHECK_API_KEY, refreshTokenString );
	}

	@Override
	public void authenticationFailed( final Throwable reason ) {
		logger.error( "Authentication failed: ", reason );
	}

}
