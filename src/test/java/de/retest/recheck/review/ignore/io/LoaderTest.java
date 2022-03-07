package de.retest.recheck.review.ignore.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoaderTest {

	Loader<String> cut;

	@BeforeEach
	void setUp() {
		cut = new StringLoader();
	}

	@Test
	void load_should_return_string() throws Exception {
		assertThat( cut.load( "foo" ) ).hasValue( "foo" );
	}

	@Test
	void load_should_return_empty_when_ignore() throws Exception {
		assertThat( cut.load( "ignore" ) ).isNotPresent();
	}

	@Test
	void save_should_return_the_same_string() throws Exception {
		assertThat( cut.save( "foo" ) ).isEqualTo( "foo" );
		assertThat( cut.save( "ignore" ) ).isEqualTo( "ignore" );
	}

	@Test
	void load_should_properly_ignore_failed_attempts() throws Exception {
		assertThat( cut.load( Stream.of( "foo", "ignore", "bar" ) ) ).contains( "foo", "bar" );
	}

	@Test
	void save_with_stream_should_properly_delegate() throws Exception {
		assertThat( cut.save( Stream.of( "foo", "ignore", "bar" ) ) ).contains( "foo", "ignore", "bar" );
	}

	static class StringLoader implements Loader<String> {

		@Override
		public Optional<String> load( final String line ) {
			return "ignore".equals( line ) ? Optional.empty() : Optional.of( line );
		}

		@Override
		public String save( final String ignore ) {
			return ignore;
		}
	}
}
