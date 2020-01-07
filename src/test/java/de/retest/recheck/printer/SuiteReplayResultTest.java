package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

	@Test
	void constructor_without_test_source_root_should_be_empty() {
		final SuiteReplayResult cut = new SuiteReplayResult( "foo", 0, null, null, null );

		assertThat( cut.getTestSourceRoot() ).isEmpty();
	}

	@Test
	void constructor_with_test_source_root_should_be_present( @TempDir final Path path ) {
		final SuiteReplayResult cut = new SuiteReplayResult( "foo", path, 0, null, null, null );

		assertThat( cut.getTestSourceRoot() ).isPresent();
	}

	@Test
	void constructor_with_test_source_root_being_null_should_not_throw_exception() {
		assertThatCode( () -> {
			new SuiteReplayResult( "foo", null, 0, null, null, null );
		} ).doesNotThrowAnyException();
	}
}
