package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.MaxPixelDiffShouldIgnore.MaxPixelDiffShouldIgnoreLoader;

class MaxPixelDiffShouldIgnoreLoaderTest {

	@Test
	void should_not_load_non_integer_and_non_double() throws Exception {
		final MaxPixelDiffShouldIgnoreLoader cut = new MaxPixelDiffShouldIgnoreLoader();

		assertThat( cut.canLoad( "foo=bar" ) ).isFalse();
		assertThat( cut.canLoad( "maxPixelDiff=baz" ) ).isFalse();
		assertThat( cut.canLoad( "maxPixelDiff=-5" ) ).isFalse();
		assertThat( cut.canLoad( "maxPixelDiff=5." ) ).isFalse();
		assertThat( cut.canLoad( "maxPixelDiff=5.0." ) ).isFalse();
		assertThat( cut.canLoad( "maxPixelDiff=5,0" ) ).isFalse();
	}

	@Test
	void should_load_integer_and_double() throws Exception {
		final MaxPixelDiffShouldIgnoreLoader cut = new MaxPixelDiffShouldIgnoreLoader();

		final String intDiff = "maxPixelDiff=5";
		assertThat( cut.canLoad( intDiff ) ).isTrue();
		assertThat( cut.load( intDiff ) ).hasFieldOrPropertyWithValue( "maxPixelDiff", 5.0 );

		final String doubleDiff = "maxPixelDiff=5.0";
		assertThat( cut.canLoad( doubleDiff ) ).isTrue();
		assertThat( cut.load( doubleDiff ) ).hasFieldOrPropertyWithValue( "maxPixelDiff", 5.0 );
	}

}
