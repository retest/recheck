package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;

class TestReportPrinterTest {

	@Test
	void toString_should_print_if_no_differences() {
		final TestReport testReport = mock( TestReport.class );
		when( testReport.isEmpty() ).thenReturn( true );

		final TestReportPrinter cut = new TestReportPrinter( DefaultValueFinderProvider.none() );

		assertThat( cut.toString( testReport ) ).isEqualTo( "No differences found." );
	}

	@Test
	void toString_should_filter_empty_suites() {
		final SuiteReplayResult emptySuite = mock( SuiteReplayResult.class );
		when( emptySuite.isEmpty() ).thenReturn( true );

		final SuiteReplayResult notEmptySuite = mock( SuiteReplayResult.class );
		when( notEmptySuite.isEmpty() ).thenReturn( false );

		final TestReport testReport = mock( TestReport.class );
		when( testReport.getSuiteReplayResults() ).thenReturn( Arrays.asList( emptySuite, notEmptySuite ) );

		final SuiteReplayResultPrinter delegate = mock( SuiteReplayResultPrinter.class );
		final String toStringPerSuite = "foo bar baz";
		when( delegate.toString( any(), any() ) ).thenReturn( toStringPerSuite );

		final TestReportPrinter cut = new TestReportPrinter( delegate );

		assertThat( cut.toString( testReport ) ).as( "should filter empty suites" ).isEqualTo( toStringPerSuite );
	}
}
