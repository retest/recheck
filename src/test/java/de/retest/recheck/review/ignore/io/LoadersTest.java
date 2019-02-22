package de.retest.recheck.review.ignore.io;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.ShouldIgnore;

class LoadersTest {

	@Test
	void test() {
		final String[] lines = new String[] { //
				"# This is a comment", //
				"matcher: id=title, key: font", //
				"matcher: id=banner", //
				"matcher: retestid=banner", //
				"matcher: retestid=banner, key: outline", //
		};
		final List<ShouldIgnore> ignores = Loaders.load( Arrays.asList( lines ).stream() ) //
				.filter( ShouldIgnore.class::isInstance ) //
				.map( ShouldIgnore.class::cast ) //
				.collect( Collectors.toList() );
		Assertions.assertThat( ignores.size() ).isEqualTo( 5 );
	}

}
