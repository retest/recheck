package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;

class TestReportPrinterTest {

	@Test
	void toString_should_not_print_if_no_differences() {
		final SuiteReplayResult emptySuite = mock( SuiteReplayResult.class );
		when( emptySuite.isEmpty() ).thenReturn( true );

		final TestReport testReport = mock( TestReport.class );
		when( testReport.getSuiteReplayResults() ).thenReturn( Collections.singletonList( emptySuite ) );

		final TestReportPrinter cut = new TestReportPrinter( DefaultValueFinderProvider.none() );

		assertThat( cut.toString( testReport ) ).isEmpty();
	}
}
