package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ReplayResultProvider;
import de.retest.report.SuiteReplayResult;

class ReplayResultProviderTest {

	@Test
	void get_suite_should_return_a_new_suite_if_current_suite_is_null() throws Exception {
		final String suiteName = "de.retest.foo";
		SuiteReplayResult currentSuite = ReplayResultProvider.getTestInstance().getSuite( suiteName );
		assertThat( currentSuite.getSuiteName() ).isEqualTo( suiteName );
	}

	@Test
	void get_suite_should_return_the_same_object_if_current_suite_is_equal() throws Exception {
		final String suiteName = "de.retest.foobar";
		final ReplayResultProvider cut = ReplayResultProvider.getTestInstance();
		SuiteReplayResult currentSuite = cut.getSuite( suiteName );
		SuiteReplayResult nextSuite = cut.getSuite( suiteName );
		assertThat( nextSuite ).isEqualTo( currentSuite );
	}

	@Test
	void get_suite_should_return_next_object_if_current_suite_is_not_equal() throws Exception {
		final String suiteName = "de.retest.foo";
		final String nextSuiteName = "de.retest.foobar";
		final ReplayResultProvider cut = ReplayResultProvider.getTestInstance();
		SuiteReplayResult currentSuite = cut.getSuite( suiteName );
		SuiteReplayResult nextSuite = cut.getSuite( nextSuiteName );
		assertThat( nextSuite.getSuiteName() ).isEqualTo( nextSuiteName );
	}
}
