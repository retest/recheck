package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementIdMatcher.ElementIdMatcherLoader;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;

class ElementIdMatcherLoaderTest {

	ElementIdMatcher matcher;
	ElementIdMatcherLoader cut;

	@BeforeEach
	void setUp() {
		cut = new ElementIdMatcherLoader();

		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div" ), "div" );
		idAttribs.add( new StringAttribute( "id", "abc" ) );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), new MutableAttributes().immutable() );

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
