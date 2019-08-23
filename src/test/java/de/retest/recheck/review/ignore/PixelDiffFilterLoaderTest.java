package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;

class PixelDiffFilterLoaderTest {

	@Test
	void should_not_load_non_integer_and_non_double() throws Exception {
		final PixelDiffFilterLoader cut = new PixelDiffFilterLoader();

		assertThat( cut.canLoad( "foo=bar" ) ).isFalse();
		assertThat( cut.canLoad( "pixel-diff=baz" ) ).isFalse();
		assertThat( cut.canLoad( "pixel-diff=-5" ) ).isFalse();
		assertThat( cut.canLoad( "pixel-diff=5." ) ).isFalse();
		assertThat( cut.canLoad( "pixel-diff=5.0." ) ).isFalse();
		assertThat( cut.canLoad( "pixel-diff=5,0" ) ).isFalse();
	}

	@Test
	void should_load_integer_and_double() throws Exception {
		final PixelDiffFilterLoader cut = new PixelDiffFilterLoader();

		final String intDiff = "pixel-diff=5";
		assertThat( cut.canLoad( intDiff ) ).isTrue();
		final PixelDiffFilter loadedIntDiff = cut.load( intDiff );
		assertThat( loadedIntDiff.isSpecifiedAsDouble() ).isFalse();
		assertThat( loadedIntDiff.getPixelDiff() ).isEqualTo( 5.0 );

		final String doubleDiff = "pixel-diff=5.0";
		assertThat( cut.canLoad( doubleDiff ) ).isTrue();
		final PixelDiffFilter loadedDoubleDiff = cut.load( doubleDiff );
		assertThat( loadedDoubleDiff.isSpecifiedAsDouble() ).isTrue();
		assertThat( loadedDoubleDiff.getPixelDiff() ).isEqualTo( 5.0 );
	}

}
