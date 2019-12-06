package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.ElementFilter.ElementFilterLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;

class ElementFilterLoaderTest {

	ElementFilterLoader cut;
	ElementFilter ignore;

	@BeforeEach
	void setUp() {
		cut = new ElementFilterLoader();

		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div" ), "div" );
		idAttribs.add( new StringAttribute( "id", "abc" ) );
		final Element element = Element.create( "retestId", mock( Element.class ),
				new IdentifyingAttributes( idAttribs ), new MutableAttributes().immutable() );

		final ElementIdMatcher matcher = new ElementIdMatcher( element );
		ignore = new ElementFilter( matcher );
	}

	@Test
	void ignore_element_by_tag_should_work() {
		cut = new ElementFilterLoader();
		final ElementFilter filter = cut.load( "matcher: type=meta" ).get();

		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "html/meta" ), "meta" ),
				new MutableAttributes().immutable() );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );

		assertThat( filter.matches( element ) ).isTrue();
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: id=abc";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}

	@Test
	void load_with_invalid_matcher_should_not_load_parent() throws Exception {
		final String line = "matcher: foo=bar";
		assertThat( cut.load( line ) ).isNotPresent();
	}
}
