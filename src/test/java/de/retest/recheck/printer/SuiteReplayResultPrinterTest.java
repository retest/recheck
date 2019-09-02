package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.descriptors.GroundState;

class SuiteReplayResultPrinterTest {

	@Test
	void toString_should_not_print_if_no_differences() {
		final SuiteReplayResultPrinter cut = new SuiteReplayResultPrinter( DefaultValueFinderProvider.none() );

		final TestReplayResult emptyTestResult = mock( TestReplayResult.class );
		when( emptyTestResult.isEmpty() ).thenReturn( true );

		final SuiteReplayResult replayResult =
				new SuiteReplayResult( "suite", 0, mock( GroundState.class ), "uuid", mock( GroundState.class ) );
		replayResult.addTest( emptyTestResult );

		assertThat( cut.toString( replayResult ) ).isEmpty();
	}
}
