package de.retest.recheck.auth;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.prefs.Preferences;

import de.retest.recheck.Rehub;
import de.retest.recheck.persistence.CloudPersistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RehubAuthenticationHandler implements AuthenticationHandler {

	@Override
	public void showWebLoginUri( final URI loginUri ) {
		try {
			Desktop.getDesktop().browse( loginUri );
		} catch ( final IOException e ) {
			log.error( "Error opening browser for '{}'.", loginUri );
		}
	}

	@Override
	public void loginPerformed( final String token ) {
		log.info( "Login successful." );
		log.info( "Please use this token for your CI: {}", token );
		Preferences.userNodeForPackage( Rehub.class ).put( CloudPersistence.RECHECK_API_KEY, token );
	}

	@Override
	public void loginFailed( final Throwable reason ) {
		log.error( "Login failed: ", reason );
	}

	@Override
	public String getOfflineToken() {
		return Rehub.getRecheckApiKey();
	}

}
