package de.retest.recheck.review.ignore.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.ShouldIgnore;

class LoadersTest {

	@Test
	void test() {
		final String[] lines = new String[] { //
				"# This is a comment", //
				"matcher: retestid=title, key: font", //
				"matcher: retestid=banner", //
				"matcher: retestid=banner", //
				"matcher: retestid=banner, key: outline", //
				"matcher: key: outline", //
				"matcher: xpath=/html[1]/div[1]/div[1]/div[1]", //
				"matcher: xpath=/html[1]/div[1]/div[1]/div[2], key: background-url" //
		};
		final List<ShouldIgnore> ignores = Loaders.load( Arrays.asList( lines ).stream() ) //
				.filter( ShouldIgnore.class::isInstance ) //
				.map( ShouldIgnore.class::cast ) //
				.collect( Collectors.toList() );
		assertThat( ignores.size() ).isEqualTo( 7 );

		assertThat( ignores.toString() ).isEqualTo( "[" //
				+ "# This is a comment, " //
				+ "matcher: id=title, key: font, " //
				+ "matcher: id=banner, " //
				+ "matcher: retestid=banner, " //
				+ "matcher: retestid=banner, key: outline, " //
				+ "matcher: key: outline, " //
				+ "matcher: xpath=/html[1]/div[1]/div[1]/div[1], " //
				+ "matcher: xpath=/html[1]/div[1]/div[1]/div[2], key: background-url" //
				+ "]" );
	}

}
