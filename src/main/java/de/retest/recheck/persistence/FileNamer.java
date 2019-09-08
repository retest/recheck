package de.retest.recheck.persistence;

import static de.retest.recheck.Properties.GOLDEN_MASTER_FILE_EXTENSION;
import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides file names, respectively, file paths for both Golden Masters and test reports.
 *
 * With the {@link DefaultFileNamer}, these follow the format
 * <code>${TEST_CLASS_NAME}/${TEST_METHOD_NAME}.${STEP_NAME}</code>, whereas test report files only use
 * <code>${TEST_CLASS_NAME}</code>.
 */
public interface FileNamer {

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
	 * @return The file to use for storing or retrieving the Golden Master with the given params.
	 */
	File getGoldenMaster( final String suitename, final String testname, final String checkname );

	/**
	 * @deprecated Because the extension for the Golden Master is fixed
	 *
	 * @param extension
	 *            The extension to use for the file. Obsolete as the Golden Master extension cannot change.
	 * @return The file to use for storing or retrieving the Golden Master with the given params.
	 */
	@Deprecated
	default File getFile( final String extension ) {
		throw new UnsupportedOperationException( "This method should not be used." );
	}

	/**
	 * Get the result file for the current suite, which this interface makes no assumptions about how it is contrived.
	 *
	 * @param suitename
	 *            The name to use for the suite to construct the path for the file.
	 * @return The file to use for storing or retrieving the report with the given param.
	 */
	File getReport( final String suitename );

	/**
	 * @deprecated Because the extension for the result files is fixed and this method is hard to implement/override
	 *             without explicitly passing a suitename. Use {@link #getReport(String)} instead.
	 *
	 * @param extension
	 *            The extension to use for the file. Obsolete as the report extension cannot change.
	 * @return The file to use for storing or retrieving the report with the given param.
	 */
	@Deprecated
	default File getResultFile( final String extension ) {
		throw new UnsupportedOperationException( "This method should not be used." );
	}

	/**
	 * @see FileNamer
	 */
	public static class DefaultFileNamer implements FileNamer {

		private final Path goldenMasterPath;
		private final Path reportPath;

		public DefaultFileNamer( final Path goldenMasterPath, final Path reportPath ) {
			this.goldenMasterPath = goldenMasterPath;
			this.reportPath = reportPath;
		}

		@Override
		public File getGoldenMaster( final String suitename, final String testname, final String checkname ) {
			String fileName = testname + "." + checkname;
			if ( StringUtils.isEmpty( testname ) ) {
				fileName = checkname;
			}
			return getSuitsFolder( suitename ).resolve( Paths.get( fileName + GOLDEN_MASTER_FILE_EXTENSION ) ).toFile();
		}

		@Override
		public File getReport( final String suitename ) {
			return reportPath.resolve( Paths.get( suitename + TEST_REPORT_FILE_EXTENSION ) ).toFile();
		}

		@Override
		public Path getSuitsFolder( final String suitename ) {
			return goldenMasterPath.resolve( Paths.get( suitename ) );
		}

	}
}
