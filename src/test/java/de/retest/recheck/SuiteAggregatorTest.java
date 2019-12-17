package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;

class SuiteAggregatorTest {

	@Test
	void get_suite_should_return_a_new_suite_if_current_suite_is_null() throws Exception {
		final String suiteName = "de.retest.foo";
		final SuiteReplayResult currentSuite = SuiteAggregator.getTestInstance().getSuite( suiteName );
		assertThat( currentSuite.getName() ).isEqualTo( suiteName );
	}

	@Test
	void get_suite_should_return_the_same_object_if_current_suite_is_equal() throws Exception {
		final String suiteName = "de.retest.bar";
		final SuiteAggregator cut = SuiteAggregator.getTestInstance();
		final SuiteReplayResult currentSuite = cut.getSuite( suiteName );
		final SuiteReplayResult nextSuite = cut.getSuite( suiteName );
		assertThat( nextSuite ).isEqualTo( currentSuite );
	}

	@Test
	void get_suite_should_return_next_object_if_current_suite_is_not_equal() throws Exception {
		final String suiteName = "de.retest.foo";
		final String nextSuiteName = "de.retest.bar";
		final SuiteAggregator cut = SuiteAggregator.getTestInstance();
		final SuiteReplayResult currentSuite = cut.getSuite( suiteName );
		final SuiteReplayResult nextSuite = cut.getSuite( nextSuiteName );
		assertThat( currentSuite.getName() ).isEqualTo( suiteName );
		assertThat( nextSuite.getName() ).isEqualTo( nextSuiteName );
	}

	@Test
	void aggregated_test_report_should_enclose_all_suites() throws Exception {
		final String suiteName = "de.retest.foo";
		final String nextSuiteName = "de.retest.bar";
		final SuiteAggregator cut = SuiteAggregator.getTestInstance();

		final SuiteReplayResult currentSuite = cut.getSuite( suiteName );
		final SuiteReplayResult nextSuite = cut.getSuite( nextSuiteName );
		final TestReport aggregatedTestReport = cut.getAggregatedTestReport();

		assertThat( aggregatedTestReport.getSuiteReplayResults() ).containsExactly( currentSuite, nextSuite );
	}
}
