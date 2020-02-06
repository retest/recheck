package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.ColorDiffFilter.ColorDiffFilterLoader;

public class ColorDiffFilterLoaderTest {

	@Test
	void should_not_load_non_integer_and_non_double() throws Exception {
		final ColorDiffFilterLoader cut = new ColorDiffFilterLoader();

		assertThat( cut.load( "foo=bar" ) ).isNotPresent();
		assertThat( cut.load( "color-diff=baz" ) ).isNotPresent();
		assertThat( cut.load( "color-diff=-5" ) ).isNotPresent();
		assertThat( cut.load( "color-diff=5." ) ).isNotPresent();
		assertThat( cut.load( "color-diff=5.0." ) ).isNotPresent();
		assertThat( cut.load( "color-diff=5,0" ) ).isNotPresent();
	}

	@Test
	void should_load_integer_and_double() throws Exception {
		final ColorDiffFilterLoader cut = new ColorDiffFilterLoader();

		final String intDiff = "color-diff=5";
		assertThat( cut.load( intDiff ) ).isPresent();
		final ColorDiffFilter loadedIntDiff = cut.load( intDiff ).get();
		assertThat( loadedIntDiff.getColorDiff() ).isEqualTo( 5.0 );

		final String doubleDiff = "color-diff=5.0";
		assertThat( cut.load( doubleDiff ) ).isPresent();
		final ColorDiffFilter loadedDoubleDiff = cut.load( doubleDiff ).get();
		assertThat( loadedDoubleDiff.getColorDiff() ).isEqualTo( 5.0 );
	}
}
