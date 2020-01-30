package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;

class AttributeFilterLoaderTest {

	final AttributeFilterLoader cut = new AttributeFilterLoader();

	@Test
	void save_should_produce_same_as_was_given_to_load_for_simple_line() {
		final String line = "attribute=text";
		assertThat( cut.load( line ) ).map( filter -> cut.save( filter ) ).hasValue( line );
	}

	@Test
	void save_should_produce_same_as_was_given_to_load_for_chained_filter() {
		final String line = "attribute=outline, pixel-diff=5px";
		assertThat( cut.load( line ).map( filter -> cut.save( filter ) ).get() ).isEqualTo( line );
	}

	@Test
	void chained_filters_should_load_correctly() {
		final Filter loaded = cut.load( "attribute=outline, pixel-diff=5px" ).get();
		assertThat( loaded ).isInstanceOf( AllMatchFilter.class );
		assertThat( ((AllMatchFilter) loaded).getFilters() ).hasSize( 2 );
	}
}
