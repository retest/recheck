package de.retest.recheck;

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
import de.retest.recheck.util.ApprovalsUtil;

class AssertionMessageTest {

	@Test
	void no_golden_master_message_should_be_formatted_properly() throws Exception {
		final ActionReplayResult actionReplayResult0 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult0.getGoldenMasterPath() ).thenReturn( "/gm/path/0" );

		final ActionReplayResult actionReplayResult1 = mock( NoGoldenMasterActionReplayResult.class );
		when( actionReplayResult1.getGoldenMasterPath() ).thenReturn( "/gm/path/0" );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );
		testReplayResult.addAction( actionReplayResult0 );
		testReplayResult.addAction( actionReplayResult1 );

		final AssertionMessage cut = new AssertionMessage( "SomeSuite", null, testReplayResult, null, null );

		ApprovalsUtil.verify( cut );
	}

	@Test
	void differences_message_should_be_formatted_properly() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.getPath() ).thenReturn( "foo/bar/baz" );

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

		final TestReplayResultPrinter testReplayResultPrinter = mock( TestReplayResultPrinter.class );
		when( testReplayResultPrinter.toString( testReplayResult ) ).thenReturn( "\tSome diff\n\tAnother diff" );

		final File resultFile = mock( File.class );
		when( resultFile.getAbsolutePath() ).thenReturn( "/report/path" );

		final AssertionMessage cut = new AssertionMessage( "SomeSuite", uniqueDifferences, testReplayResult,
				testReplayResultPrinter, resultFile );

		ApprovalsUtil.verify( cut );
	}

}
