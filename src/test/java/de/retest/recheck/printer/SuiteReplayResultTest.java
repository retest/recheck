package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;

class SuiteReplayResultTest {

	@Test
	void isEmpty_should_return_true_if_no_differences() throws Exception {
		final TestReplayResult test = mock( TestReplayResult.class );
		when( test.isEmpty() ).thenReturn( true );

		final SuiteReplayResult cut = new SuiteReplayResult( "foo", 0, null, null, null );
		cut.addTest( test );

		assertThat( cut.isEmpty() ).isTrue();
	}

	@Test
	void isEmpty_should_return_false_if_differences() throws Exception {
		final TestReplayResult test = mock( TestReplayResult.class );
		when( test.isEmpty() ).thenReturn( false );

		final SuiteReplayResult cut = new SuiteReplayResult( "foo", 0, null, null, null );
		cut.addTest( test );

		assertThat( cut.isEmpty() ).isFalse();
	}
}
