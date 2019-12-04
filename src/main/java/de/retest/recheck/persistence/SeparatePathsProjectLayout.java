package de.retest.recheck.persistence;

import static de.retest.recheck.RecheckProperties.GOLDEN_MASTER_FILE_EXTENSION;
import static de.retest.recheck.RecheckProperties.TEST_REPORT_FILE_EXTENSION;

import java.nio.file.Path;

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
	public Path getGoldenMaster( final String suiteName, final String testName, final String checkName ) {
		String fileName = testName + "." + checkName;
		if ( StringUtils.isEmpty( testName ) ) {
			fileName = checkName;
		}
		return getSuiteFolder( suiteName ).resolve( fileName + GOLDEN_MASTER_FILE_EXTENSION );
	}

	@Override
	public Path getReport( final String suiteName ) {
		return reportPath.resolve( suiteName + TEST_REPORT_FILE_EXTENSION );
	}

	@Override
	public Path getSuiteFolder( final String suiteName ) {
		return goldenMasterPath.resolve( suiteName );
	}
}
