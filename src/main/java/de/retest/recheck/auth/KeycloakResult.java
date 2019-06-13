package de.retest.recheck.auth;

import java.io.IOException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeycloakResult {
	private final String code;
	private final String error;
	private final String errorDescription;
	private final IOException errorException;
	private final String state;
}
