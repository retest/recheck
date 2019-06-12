package de.retest.recheck.persistence;

import static de.retest.recheck.Properties.AGGREGATED_TEST_REPORT_FILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.report.SuiteReplayResult;

class RecheckTestReportUtilTest {

	File testReport;
	File testReportMissingFolders;

	@BeforeEach
	void setUp( @TempDir final Path temp ) {
		testReport = temp.resolve( "test.report" ).toFile();
		testReportMissingFolders = temp.resolve( "missing/folder/test.report" ).toFile();
	}

	@Test
	void persist_should_create_report_when_there_are_no_diffs() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 0 );

		RecheckTestReportUtil.persist( replayResult, testReport );

		assertThat( testReport ).exists();
	}

	@Test
	void persist_should_create_report_when_there_are_diffs() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 1 );

		RecheckTestReportUtil.persist( replayResult, testReport );

		assertThat( testReport ).exists();
	}

	@Test
	void persist_should_create_missing_folders() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 0 );

		RecheckTestReportUtil.persist( replayResult, testReportMissingFolders );

		assertThat( testReportMissingFolders ).exists();
	}

	@Test
	void persist_should_create_aggregated_test_report() throws Exception {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );

		RecheckTestReportUtil.persist( replayResult, testReport );

		final File aggregatedTestReport = new File( testReport.getParent(), AGGREGATED_TEST_REPORT_FILE_NAME );
		assertThat( aggregatedTestReport ).exists();
	}
}
