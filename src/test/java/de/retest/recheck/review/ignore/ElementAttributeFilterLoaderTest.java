package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.ElementAttributeFilter.ElementAttributeFilterLoader;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.descriptors.Element;

class ElementAttributeFilterLoaderTest {

	ElementAttributeFilterLoader cut;
	ElementAttributeFilter ignore;

	@BeforeEach
	void setUp() {
		cut = new ElementAttributeFilterLoader();

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		ignore = new ElementAttributeFilter( new ElementRetestIdMatcher( element ), "123" );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: retestid=abc, attribute: 123" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: retestid=abc, attribute: 123";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}
}
