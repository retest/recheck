package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.descriptors.GroundState;
import de.retest.recheck.ui.diff.StateDifference;

class SuiteReplayResultPrinterTest {

	@Test
	void toString_should_not_print_if_no_differences() {
		final SuiteReplayResultPrinter cut = new SuiteReplayResultPrinter( DefaultValueFinderProvider.none() );

		final TestReplayResult emptyTestResult = mock( TestReplayResult.class );
		when( emptyTestResult.isEmpty() ).thenReturn( true );

		final SuiteReplayResult replayResult =
				new SuiteReplayResult( "suite", 0, mock( GroundState.class ), "uuid", mock( GroundState.class ) );
		replayResult.addTest( emptyTestResult );

		assertThat( cut.toString( replayResult ) ).isEqualTo( "Suite 'suite' has 0 difference(s) in 1 test(s):\n" );
	}

	@Test
	void toString_with_indent_should_not_print_if_no_differences() {
		final SuiteReplayResultPrinter cut = new SuiteReplayResultPrinter( DefaultValueFinderProvider.none() );

		final SuiteReplayResult replayResult =
				new SuiteReplayResult( "suite", 0, mock( GroundState.class ), "uuid", mock( GroundState.class ) );

		assertThat( cut.toString( replayResult, "____" ) ).startsWith( "____" );
	}

	@Test
	void toString_should_print_test_properly_indented() throws Exception {
		final SuiteReplayResultPrinter cut = new SuiteReplayResultPrinter( DefaultValueFinderProvider.none() );

		final ActionReplayResult actionResult = mock( ActionReplayResult.class );
		when( actionResult.hasDifferences() ).thenReturn( true );
		when( actionResult.getStateDifference() ).thenReturn( mock( StateDifference.class ) );

		final TestReplayResult testResult = mock( TestReplayResult.class );
		when( testResult.isEmpty() ).thenReturn( false );
		when( testResult.getActionReplayResults() ).thenReturn( Collections.singletonList( actionResult ) );

		final SuiteReplayResult replayResult =
				new SuiteReplayResult( "suite", 0, mock( GroundState.class ), "uuid", mock( GroundState.class ) );
		replayResult.addTest( testResult );

		assertThat( cut.toString( replayResult ) ).isEqualTo( "Suite 'suite' has 0 difference(s) in 1 test(s):\n" //
				+ "\tTest 'null' has 0 difference(s) in 1 state(s):\n" //
				+ "\tnull resulted in:\n" );
	}
}
