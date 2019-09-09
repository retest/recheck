package de.retest.recheck.persistence;

import static de.retest.recheck.Properties.GOLDEN_MASTER_FILE_EXTENSION;
import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

/**
 * Uses two separate paths (provided in the constructor) to place the Golden Masters in a (usually git-managed) resource
 * folder, but the reports in a temporary build-artefacts folder.
 *
 * The naming of the Golden Masters follows <code>${TEST_CLASS_NAME}/${TEST_METHOD_NAME}.${STEP_NAME}</code>, whereas
 * test report files only use <code>${TEST_CLASS_NAME}</code>.
 */
public class SeparatePathsProjectLayout implements ProjectLayout {

	private final Path goldenMasterPath;
	private final Path reportPath;

	public SeparatePathsProjectLayout( final Path goldenMasterPath, final Path reportPath ) {
		this.goldenMasterPath = goldenMasterPath;
		this.reportPath = reportPath;
	}

	@Override
	public Path getGoldenMaster( final String suitename, final String testname, final String checkname ) {
		String fileName = testname + "." + checkname;
		if ( StringUtils.isEmpty( testname ) ) {
			fileName = checkname;
		}
		return getSuitsFolder( suitename ).resolve( Paths.get( fileName + GOLDEN_MASTER_FILE_EXTENSION ) );
	}

	@Override
	public Path getReport( final String suitename ) {
		return reportPath.resolve( Paths.get( suitename + TEST_REPORT_FILE_EXTENSION ) );
	}

	@Override
	public Path getSuitsFolder( final String suitename ) {
		return goldenMasterPath.resolve( Paths.get( suitename ) );
	}
}
