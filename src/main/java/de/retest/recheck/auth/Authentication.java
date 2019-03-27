package de.retest.recheck.auth;

import java.io.IOException;
import java.io.InputStream;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.adapters.ServerRequest.HttpFailure;

public class Authentication {

	private static final String KEYCLOAK_JSON = "META-INF/keycloak.json";
	private static InputStream config;
	private static KeycloakDeployment deployment;
	private final String refreshToken;

	private static Authentication instance;

	private Authentication( final String refreshToken ) {
		this.refreshToken = refreshToken;
		config = Thread.currentThread().getContextClassLoader().getResourceAsStream( KEYCLOAK_JSON );
		deployment = KeycloakDeploymentBuilder.build( config );
	}

	public static Authentication getInstance( final String refreshToken ) {
		if ( instance == null ) {
			instance = new Authentication( refreshToken );
		}
		return instance;
	}

	public String exchangeToken() throws IOException, HttpFailure {
		return ServerRequest.invokeRefresh( deployment, refreshToken ).getToken();
	}

}
