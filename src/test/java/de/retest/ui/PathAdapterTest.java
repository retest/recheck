package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathAdapterTest {

	@Test
	public void paths_should_exist_exactly_once_when_unmarshalling() throws Exception {
		final PathAdapter cut = new PathAdapter();
		final Path p0 = cut.unmarshal( Path.fromString( "/foo" ) );
		final Path p1 = cut.unmarshal( Path.fromString( "/foo/bar" ) );
		final Path p2 = cut.unmarshal( Path.fromString( "/foo/bar/baz" ) );

		assertThat( cut.unmarshal( Path.fromString( "/foo" ) ) ).isSameAs( p0 );
		assertThat( cut.unmarshal( Path.fromString( "/foo/bar" ) ) ).isSameAs( p1 );
		assertThat( cut.unmarshal( Path.fromString( "/foo/bar/baz" ) ) ).isSameAs( p2 );
	}

}
