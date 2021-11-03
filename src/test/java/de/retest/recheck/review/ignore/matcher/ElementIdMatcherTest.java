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

class ElementIdMatcherTest {

	ElementIdMatcher cut;

	@BeforeEach
	void setUp() {
		final Element element = createElementWithId( "someDiv" );

		cut = new ElementIdMatcher( element );
	}

	@Test
	void should_match_when_id_is_equal() {
		final Element element = createElementWithId( "someDiv" );

		assertThat( cut ).accepts( element );
	}

	@Test
	void should_not_match_when_id_is_not_equal() {
		final Element element = createElementWithId( "otherDiv" );

		assertThat( cut ).rejects( element );
	}

	private Element createElementWithId( final String id ) {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div" ), "div" );
		idAttribs.add( new StringAttribute( "id", id ) );
		return Element.create( "retestId", mock( Element.class ), new IdentifyingAttributes( idAttribs ),
				new MutableAttributes().immutable() );
	}
}
