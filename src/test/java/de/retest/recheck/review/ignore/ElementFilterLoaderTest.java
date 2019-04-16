package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.ElementFilter.ElementFilterLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementFilterLoaderTest {

	ElementFilterLoader cut;
	ElementFilter ignore;

	@BeforeEach
	void setUp() {
		cut = new ElementFilterLoader();

		final IdentifyingAttributes attributes = mock( IdentifyingAttributes.class );
		when( attributes.get( "id" ) ).thenReturn( "abc" );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attributes );

		final ElementIdMatcher matcher = new ElementIdMatcher( element );
		ignore = new ElementFilter( matcher );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: id=abc";
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}
}
