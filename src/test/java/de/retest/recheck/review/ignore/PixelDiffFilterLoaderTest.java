package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;

class PixelDiffFilterLoaderTest {

	@Test
	void should_not_load_non_integer_and_non_double() throws Exception {
		final PixelDiffFilterLoader cut = new PixelDiffFilterLoader();

		assertThat( cut.load( "foo=bar" ) ).isNotPresent();
		assertThat( cut.load( "pixel-diff=baz" ) ).isNotPresent();
		assertThat( cut.load( "pixel-diff=-5" ) ).isNotPresent();
		assertThat( cut.load( "pixel-diff=5." ) ).isNotPresent();
		assertThat( cut.load( "pixel-diff=5.0." ) ).isNotPresent();
		assertThat( cut.load( "pixel-diff=5,0" ) ).isNotPresent();
	}

	@Test
	void should_load_integer_and_double() throws Exception {
		final PixelDiffFilterLoader cut = new PixelDiffFilterLoader();

		final String intDiff = "pixel-diff=5";
		assertThat( cut.load( intDiff ) ).isPresent();
		final PixelDiffFilter loadedIntDiff = cut.load( intDiff ).get();
		assertThat( loadedIntDiff.getPixelDiff() ).isEqualTo( 5.0 );
		assertThat( loadedIntDiff ).hasToString( intDiff );

		final String doubleDiff = "pixel-diff=5.0";
		assertThat( cut.load( doubleDiff ) ).isPresent();
		final PixelDiffFilter loadedDoubleDiff = cut.load( doubleDiff ).get();
		assertThat( loadedDoubleDiff.getPixelDiff() ).isEqualTo( 5.0 );
		assertThat( loadedDoubleDiff ).hasToString( doubleDiff );
	}

}
