package de.retest.recheck.auth;

import java.net.URI;

public interface AuthenticationHandler {

	void showWebLoginUri( URI loginUri );

	void authenticated();

	void authenticationFailed( Throwable reason );

}
