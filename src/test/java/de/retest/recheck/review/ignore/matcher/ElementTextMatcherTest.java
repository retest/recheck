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

class ElementTextMatcherTest {

	private ElementTextMatcher matcher;

	@BeforeEach
	void setUp() {
		final Element element = mockElementWithTextValue( "Sign in" );

		matcher = new ElementTextMatcher( element );
	}

	@Test
	void should_match_when_type_is_equal() {
		final Element element = mockElementWithTextValue( "Sign in" );

		assertThat( matcher ).accepts( element );
	}

	@Test
	void should_not_match_when_text_is_not_equal() {
		final Element element = mockElementWithTextValue( "Log in" );

		assertThat( matcher ).rejects( element );
	}

	private Element mockElementWithTextValue( final String textValue ) {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div/a" ), "a" );
		idAttribs.add( new StringAttribute( ElementTextMatcher.TEXT_KEY, textValue ) );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), new MutableAttributes().immutable() );
		return element;
	}
}
