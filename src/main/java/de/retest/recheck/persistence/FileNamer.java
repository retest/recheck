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
	default File getGoldenMaster( final String suitename, final String testname, final String checkname ) {
		return getFile( Properties.GOLDEN_MASTER_FILE_EXTENSION );
	}

	/**
	 * @deprecated Because the extension for the Golden Master is fixed
	 */
	@Deprecated
	File getFile( String extension );

	/**
	 * Get the result file for the current suite, which this interface makes no assumptions about how it is contrived.
	 */
	default File getReport( final String suitename ) {
		return getResultFile( Properties.TEST_REPORT_FILE_EXTENSION );
	}

	/**
	 * @deprecated Because the extension for the result files is fixed and this method is hard to implement/override
	 *             without explicitly passing a suitename. Use {@link #getReport(String)} instead.
	 */
	@Deprecated
	File getResultFile( final String extension );

}
