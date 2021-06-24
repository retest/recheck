package de.retest.recheck.persistence;

import static de.retest.recheck.RecheckProperties.AGGREGATED_TEST_REPORT_FILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.diff.ElementBuilder;
import de.retest.recheck.ui.diff.ElementBuilder.child2;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

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
		final SuiteReplayResult replayResult = new SuiteReplayResult( "test", 23, null, "00", null );

		RecheckTestReportUtil.persist( replayResult, testReport );

		assertThat( testReport ).exists();
	}

	@Test
	void persist_should_create_report_when_there_are_diffs() {
		final SuiteReplayResult replayResult = new SuiteReplayResult( "test", 23, null, "00", null );
		replayResult.addTest( createDummyReplayResultWithOneDiff() );

		RecheckTestReportUtil.persist( replayResult, testReport );

		assertThat( testReport ).exists();
	}

	TestReplayResult createDummyReplayResultWithOneDiff() {
		final RootElement root = new RootElement( "retestId",
				new IdentifyingAttributes( ElementBuilder
						.createIdentifyingAttribute( de.retest.recheck.ui.Path.fromString( "comp1" ), child2.class ) ),
				new Attributes(), null, "screen", 1, "title" );
		final ActionReplayResult test = ActionReplayResult.createActionReplayResult( ActionReplayData.empty(),
				new StateDifference( Arrays.asList( new RootElementDifference(
						new ElementDifference( root, null, null, null, null, Collections.emptyList() ), root,
						null ) ) ),
				1, null );

		final TestReplayResult testReplayResult = new TestReplayResult( "test", 1 );
		testReplayResult.addAction( test );
		return testReplayResult;
	}

	@Test
	void persist_should_create_missing_folders() {
		final SuiteReplayResult replayResult = new SuiteReplayResult( "test", 23, null, "00", null );

		RecheckTestReportUtil.persist( replayResult, testReportMissingFolders );

		assertThat( testReportMissingFolders ).exists();
	}

	@Test
	void persist_should_create_aggregated_test_report() throws Exception {
		final SuiteReplayResult replayResult = new SuiteReplayResult( "test", 23, null, "00", null );

		RecheckTestReportUtil.persist( replayResult, testReport );

		final File aggregatedTestReport = new File( testReport.getParent(), AGGREGATED_TEST_REPORT_FILE_NAME );
		assertThat( aggregatedTestReport ).exists();
	}
}
