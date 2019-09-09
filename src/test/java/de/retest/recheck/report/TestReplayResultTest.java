package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ui.diff.LeafDifference;

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

	@Test
	void getDifferences_should_retrieve_all_child_differences() {
		final TestReplayResult cut = new TestReplayResult( "foo", 5 );
		cut.addAction( action() );
		cut.addAction( action() );
		cut.addAction( action() );

		assertThat( cut.getDifferences() ).hasSize( 15 );
	}

	@Test
	void isEmpty_should_return_true_if_no_differences() throws Exception {
		final ActionReplayResult action = mock( ActionReplayResult.class );

		final TestReplayResult cut = new TestReplayResult( "foo", 1 );
		cut.addAction( action );

		assertThat( cut.isEmpty() ).isTrue();
	}

	@Test
	void isEmpty_should_return_false_if_differences() throws Exception {
		final ActionReplayResult action = mock( ActionReplayResult.class, RETURNS_DEEP_STUBS );
		when( action.getAllElementDifferences().size() ).thenReturn( 1 );

		final TestReplayResult cut = new TestReplayResult( "foo", 2 );
		cut.addAction( action );

		assertThat( cut.isEmpty() ).isFalse();
	}

	private ActionReplayResult action() {
		final ActionReplayResult replayResult = mock( ActionReplayResult.class );
		when( replayResult.getDifferences() ).thenReturn(
				Stream.generate( () -> mock( LeafDifference.class ) ).limit( 5 ).collect( Collectors.toSet() ) );
		return replayResult;
	}
}
