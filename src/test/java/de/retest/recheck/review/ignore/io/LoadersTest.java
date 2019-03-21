package de.retest.recheck.review.ignore.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class LoadersTest {

	@Test
	void test() {
		final List<String> in = Arrays.asList( "# This is a comment", //
				"matcher: id=title, attribute: font", //
				"matcher: id=banner", //
				"matcher: retestid=banner", //
				"matcher: retestid=banner, attribute: outline", //
				"\t ", // this is an empty line with invisible whitespace chars
				"attribute=outline", //
				"matcher: xpath=/html[1]/div[1]/div[1]/div[1]", //
				"matcher: xpath=/html[1]/div[1]/div[1]/div[2], attribute: background-url" );

		final List<String> out = Loaders.load( in.stream() ) //
				.map( Object::toString ) //
				.collect( Collectors.toList() );

		assertThat( out ).isEqualTo( in );
	}

}
