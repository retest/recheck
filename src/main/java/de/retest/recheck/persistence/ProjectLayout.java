package de.retest.recheck.persistence;

import java.nio.file.Path;

/**
 * Provides file paths for both Golden Masters and test reports. There exist implementations for Maven and Gradle. If
 * these do not fit your need or your layout deviates from standard, you need to implement this class.
 */
public interface ProjectLayout {

	/**
	 * Gets the base-folder of the suite where the tests are stored in. Useful to store additional data, like a
	 * suite-specific ignore file.
	 *
	 * @param suitename
	 *            The name of the suite.
	 * @return The base-folder for the given suite name.
	 */
	Path getSuitsFolder( final String suitename );

	/**
	 * Get the GoldenMaster file for the current check, which this interface makes no assumptions about how it is
	 * contrived.
	 *
	 * @param suitename
	 *            The name to use for the suite to construct the path for the file.
	 * @param testname
	 *            The name to use for the test to construct the path for the file.
	 * @param checkname
	 *            The name to use for the check to construct the path for the file.
	 * @return The path to use for storing or retrieving the Golden Master with the given params.
	 */
	Path getGoldenMaster( final String suitename, final String testname, final String checkname );

	/**
	 * Get the result file for the current suite, which this interface makes no assumptions about how it is contrived.
	 *
	 * @param suitename
	 *            The name to use for the suite to construct the path for the file.
	 * @return The path to use for storing or retrieving the report with the given param.
	 */
	Path getReport( final String suitename );
}
