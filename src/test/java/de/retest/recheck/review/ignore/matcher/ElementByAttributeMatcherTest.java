package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;

class ElementByAttributeMatcherTest {

	@Test
	void should_match_when_identifying_attribute_is_equal() {
		final ElementByAttributeMatcher matcher = new ElementByAttributeMatcher( "text", "Sign in" );
		final Element element = mockElementWithTextValue( "Sign in" );

		assertThat( matcher ).accepts( element );
	}

	@Test
	void should_not_match_when_identifying_attribute_is_not_equal() {
		final ElementByAttributeMatcher matcher = new ElementByAttributeMatcher( "text", "Sign in" );
		final Element element = mockElementWithTextValue( "Log in" );

		assertThat( matcher ).rejects( element );
	}

	@Test
	void should_match_when_nonidentifying_attribute_is_equal() {
		final ElementByAttributeMatcher matcher = new ElementByAttributeMatcher( "data-id", "sigint" );
		final Element element = mockElementWithAttributeValue( "data-id", "sigint" );

		assertThat( matcher ).accepts( element );
	}

	@Test
	void should_not_match_when_nonidentifying_attribute_is_not_equal() {
		final ElementByAttributeMatcher matcher = new ElementByAttributeMatcher( "data-id", "sigint" );
		final Element element = mockElementWithAttributeValue( "data-id", "something else" );

		assertThat( matcher ).rejects( element );
	}

	private Element mockElementWithAttributeValue( final String key, final String value ) {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div/a" ), "a" );
		final MutableAttributes attributes = new MutableAttributes();
		attributes.put( key, value );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), attributes.immutable() );
		return element;
	}

	private Element mockElementWithTextValue( final String textValue ) {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div/a" ), "a" );
		idAttribs.add( new StringAttribute( "text", textValue ) );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), new MutableAttributes().immutable() );
		return element;
	}
}
