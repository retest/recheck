package de.retest.recheck.persistence;

import lombok.Data;

/**
 * A naming strategy without magic, where the values of suiteName and testName can be changed at will. Please note that
 * the caller must ensure thread safety when executing tests in parallel.
 */
@Data
public class ExplicitMutableNamingStrategy implements NamingStrategy {

	private String suiteName;
	private String testName;

}
