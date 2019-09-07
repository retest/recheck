package de.retest.recheck.persistence;

import static de.retest.recheck.Properties.GOLDEN_MASTER_FILE_EXTENSION;
import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

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
			return goldenMasterPath.resolve( Paths.get( suitename, fileName + GOLDEN_MASTER_FILE_EXTENSION ) ).toFile();
		}

		@Override
		public File getReport( final String suitename ) {
			return reportPath.resolve( Paths.get( suitename + TEST_REPORT_FILE_EXTENSION ) ).toFile();
		}

	}
}
