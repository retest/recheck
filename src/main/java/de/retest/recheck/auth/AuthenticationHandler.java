package de.retest.recheck.auth;

import java.net.URI;

public interface AuthenticationHandler {

	void showWebLoginUri( URI loginUri );

	void loginPerformed( String token );

	void loginFailed( Throwable reason );

}
