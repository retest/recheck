package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.MatcherFilter;
import de.retest.recheck.review.ignore.PixelDiffFilter;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;

class AllMatchFilterLoaderTest extends AllMatchFilterLoader {

	final AllMatchFilterLoader cut = new AllMatchFilterLoader();

	@Test
	void chained_matcherfilter_should_give_correct_line() {
		final AllMatchFilter ignore = new AllMatchFilter( new MatcherFilter( new ElementIdMatcher( "123" ) ),
				new AttributeFilter( "outline" ), new PixelDiffFilter( "5px", 5.0 ) );
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=123, attribute=outline, pixel-diff=5px" );
	}

	@Test
	void chained_attribute_filter_should_give_correct_line() {
		// attribute=outline, pixel-diff=5px
		final AllMatchFilter ignore = new AllMatchFilter( new MatcherFilter( new ElementIdMatcher( "123" ) ),
				new AttributeFilter( "outline" ) );
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=123, attribute=outline" );
	}

	@Test
	void single_filter_line_should_be_same_as_filter() {
		final AllMatchFilter ignore = new AllMatchFilter( new MatcherFilter( new ElementIdMatcher( "123" ) ) );
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=123" );
	}

	@Test
	void should_always_return_empty_on_load() {
		assertThat( cut.load( "matcher: id=123" ) ).isEmpty();
		assertThat( cut.load( "matcher: id=123, attribute=outline" ) ).isEmpty();
		assertThat( cut.load( "matcher: id=123, attribute=outline, pixel-diff=5px" ) ).isEmpty();
	}
}
