package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementClassMatcherTest {

	Element element;
	ElementClassMatcher cut;

	@BeforeEach
	void setUp() throws Exception {
		element = mockElementWithClassValue( "some-class" );

		cut = new ElementClassMatcher( element );
	}

	@Test
	void should_match_equal_class_value() throws Exception {
		assertThat( cut ).accepts( element );
	}

	@Test
	void should_not_match_unequal_class_value() throws Exception {
		final Element element = mockElementWithClassValue( "other-class" );

		assertThat( cut ).rejects( element );
	}

	@Test
	void should_not_match_any_class_value_if_class_key_is_absent() throws Exception {
		final Element element = mockElementWithClassValue( null );

		final ElementClassMatcher cut = new ElementClassMatcher( element );

		assertThat( cut ).rejects( element );
		assertThat( cut ).rejects( this.element );
	}

	private Element mockElementWithClassValue( final String classValue ) {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( ElementClassMatcher.CLASS_KEY ) ).thenReturn( classValue );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		return element;
	}

}
