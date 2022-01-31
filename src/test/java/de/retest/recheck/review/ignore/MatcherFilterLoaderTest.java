package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.MatcherFilter.MatcherFilterLoader;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;

class MatcherFilterLoaderTest {

	MatcherFilterLoader cut;
	MatcherFilter ignore;

	@BeforeEach
	void setUp() {
		cut = new MatcherFilterLoader();
		final ElementRetestIdMatcher matcher = getRetestIdMatcher();
		ignore = new MatcherFilter( matcher );
	}

	ElementRetestIdMatcher getRetestIdMatcher() {
		final Collection<Attribute> idAttribs =
				IdentifyingAttributes.createList( Path.fromString( "html/div" ), "div" );
		idAttribs.add( new StringAttribute( "id", "abc" ) );
		final Element element = Element.create( "abc", mock( Element.class ), new IdentifyingAttributes( idAttribs ),
				new MutableAttributes().immutable() );

		return new ElementRetestIdMatcher( element );
	}

	@Test
	void ignore_element_by_tag_should_work() {
		cut = new MatcherFilterLoader();
		final Filter filter = cut.load( "matcher: type=meta" ).get();

		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "html/meta" ), "meta" ),
				new MutableAttributes().immutable() );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );

		assertThat( filter.matches( element ) ).isTrue();
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: retestid=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: id=abc";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}

	@Test
	void load_with_generic_matcher_should_load_correctly() {
		final String line = "matcher: foo=bar";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}

	@Test
	void save_chained_should_produce_correct_line() {
		final Filter ignore =
				new AllMatchFilter( new MatcherFilter( getRetestIdMatcher() ), new AttributeFilter( "123" ) );
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: retestid=abc, attribute=123" );
	}

	@Test
	void load_chained_should_produce_correct_ignore() {
		final String line = "matcher: retestid=abc, attribute=123";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}

	@Test
	void load_chained_with_generic_matcher_should_produce_correct_ignore() {
		final String line = "matcher: foo=bar, attribute=123";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}

	@Test
	void load_with_invalid_matcher_should_throw_exception() {
		final String line = "matcher: foo:bar";
		assertThrows( IllegalArgumentException.class, () -> cut.load( line ) );
	}

}
