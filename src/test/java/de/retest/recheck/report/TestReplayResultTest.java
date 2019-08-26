package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;

class TestReplayResultTest {

	@Test
	void hasNoGoldenMaster_should_return_false_if_no_specific_result_is_present() {
		final TestReplayResult cut = new TestReplayResult( "foo", 42 );
		cut.addAction( mock( ActionReplayResult.class ) );

		assertThat( cut.hasNoGoldenMaster() ).isFalse();
	}

	@Test
	void hasNoGoldenMaster_should_return_false_if_not_all_gm() {
		final TestReplayResult cut = new TestReplayResult( "foo", 42 );
		cut.addAction( mock( NoGoldenMasterActionReplayResult.class ) );
		cut.addAction( mock( ActionReplayResult.class ) );

		assertThat( cut.hasNoGoldenMaster() ).isFalse();
	}

	@Test
	void hasNoGoldenMaster_should_return_true_only_if_all_are_not_present() {
		final TestReplayResult cut = new TestReplayResult( "foo", 42 );
		cut.addAction( mock( NoGoldenMasterActionReplayResult.class ) );
		cut.addAction( mock( NoGoldenMasterActionReplayResult.class ) );

		assertThat( cut.hasNoGoldenMaster() ).isTrue();
	}
}
