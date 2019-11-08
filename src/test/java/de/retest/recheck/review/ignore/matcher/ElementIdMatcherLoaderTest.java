package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementIdMatcher.ElementIdMatcherLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementIdMatcherLoaderTest {

	ElementIdMatcher matcher;
	ElementIdMatcherLoader cut;

	@BeforeEach
	void setUp() {
		cut = new ElementIdMatcherLoader();

		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.get( "id" ) ).thenReturn( "abc" );
		matcher = new ElementIdMatcher( element );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( matcher ) ).isEqualTo( "id=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "id=abc";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}
}
