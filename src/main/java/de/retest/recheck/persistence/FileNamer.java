package de.retest.recheck.persistence;

import java.io.File;

import de.retest.recheck.Properties;

/**
 * Provides file names, respectively, file paths for both Golden Masters and test reports.
 */
public interface FileNamer {

	/**
	 * Get the GoldenMaster file for the current check, which this interface makes no assumptions about how it is
	 * contrived.
	 */
	File getGoldenMaster( final String suitename, final String testname, final String checkname );

	/**
	 * @deprecated Because the extension for the Golden Master is fixed
	 */
	@Deprecated
	default File getFile( final String extension ) {
		throw new UnsupportedOperationException( "This method should not be used." );
	}

	/**
	 * Get the result file for the current suite, which this interface makes no assumptions about how it is contrived.
	 */
	File getReport( final String suitename );

	/**
	 * @deprecated Because the extension for the result files is fixed and this method is hard to implement/override
	 *             without explicitly passing a suitename. Use {@link #getReport(String)} instead.
	 */
	@Deprecated
	default File getResultFile( final String extension ) {
		throw new UnsupportedOperationException( "This method should not be used." );
	}

}
