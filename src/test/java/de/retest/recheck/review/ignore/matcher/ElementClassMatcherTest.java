package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;

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

	@Test
	void should_match_multiple_equal_class_values() throws Exception {
		final Element oneTwo = mockElementWithClassValue( "one two" );

		final ElementClassMatcher cut = new ElementClassMatcher( oneTwo );

		final Element oneOne = mockElementWithClassValue( "oneone" );
		final Element two = mockElementWithClassValue( "two" );
		final Element booTwoOne = mockElementWithClassValue( "boo two one" );

		assertThat( cut ).accepts( oneTwo, booTwoOne );
		assertThat( cut ).rejects( oneOne, two );
	}

	@Test
	void should_not_match_multiple_unequal_class_values() throws Exception {
		final Element oneTwoOnone = mockElementWithClassValue( "one two onone" );

		final ElementClassMatcher cut = new ElementClassMatcher( oneTwoOnone );

		final Element three = mockElementWithClassValue( "three" );
		final Element oneThree = mockElementWithClassValue( "one three" );
		final Element threeOne = mockElementWithClassValue( "three one" );

		assertThat( cut ).rejects( three, oneThree, threeOne );
	}

	private Element mockElementWithClassValue( final String classValue ) {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div" ), "div" );
		idAttribs.add( new StringAttribute( ElementClassMatcher.CLASS_KEY, classValue ) );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), new MutableAttributes().immutable() );
		return element;
	}

}
