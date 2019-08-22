package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.retest.recheck.printer.TestReplayResultPrinter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;

class AssertionMessageTest {

	@Test
	void no_golden_master_message_should_be_formatted_properly() throws Exception {
		final String suiteName = "SomeSuite";

		final String gmPath0 = "/gm/path/0";
		final ActionReplayResult actionReplayResult0 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult0.getGoldenMasterPath() ).thenReturn( gmPath0 );

		final String gmPath1 = "/gm/path/0";
		final ActionReplayResult actionReplayResult1 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult1.getGoldenMasterPath() ).thenReturn( gmPath1 );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );
		testReplayResult.addAction( actionReplayResult0 );
		testReplayResult.addAction( actionReplayResult1 );

		final AssertionMessage cut = new AssertionMessage( suiteName, null, testReplayResult, null, null );

		assertThat( cut ).hasToString( "'" + suiteName + "':\n" //
				+ NoGoldenMasterActionReplayResult.MSG_LONG + "\n" //
				+ gmPath0 + "\n" //
				+ gmPath1 );
	}

	@Test
	void differences_message_should_be_formatted_properly() throws Exception {
		final String suiteName = "SomeSuite";

		final String elementPath = "foo/bar/baz";
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.getPath() ).thenReturn( elementPath );

		final Element absent = null;
		final Element present = mock( Element.class );
		when( present.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final InsertedDeletedElementDifference insertion =
				InsertedDeletedElementDifference.differenceFor( absent, present );
		final InsertedDeletedElementDifference deletion =
				InsertedDeletedElementDifference.differenceFor( present, absent );
		// Only inserted/deleted differences from unique differences should be accounted.
		final LeafDifference ignore = mock( LeafDifference.class );

		// Use LinkedHashSet to guarantee order.
		final Set<LeafDifference> uniqueDifferences =
				new LinkedHashSet<>( Arrays.asList( insertion, deletion, ignore ) );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );
		testReplayResult.addAction( mock( ActionReplayResult.class ) );
		testReplayResult.addAction( mock( ActionReplayResult.class ) );

		final String allDiffs = "\tSome diff\n\tAnother diff";
		final TestReplayResultPrinter testReplayResultPrinter = mock( TestReplayResultPrinter.class );
		when( testReplayResultPrinter.toString( testReplayResult ) ).thenReturn( allDiffs );

		final String reportPath = "/report/path";
		final File resultFile = mock( File.class );
		when( resultFile.getAbsolutePath() ).thenReturn( reportPath );

		final AssertionMessage cut = new AssertionMessage( suiteName, uniqueDifferences, testReplayResult,
				testReplayResultPrinter, resultFile );

		assertThat( cut ).hasToString( "A detailed report will be created at '" + reportPath + "'. " //
				+ "You can review the details by using our CLI (https://github.com/retest/recheck.cli/) or GUI (https://retest.de/review/).\n" //
				+ "\n" //
				+ "2 check(s) in '" + suiteName + "' found the following difference(s):\n" //
				+ allDiffs //
				+ "\t" + elementPath + " was inserted!\n" //
				+ "\t" + elementPath + " was deleted!" );
	}

}
