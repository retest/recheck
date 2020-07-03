package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.MatcherFilter.MatcherFilterLoader;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;

class MatcherFilterIT {

	Element html;
	Element body;
	Element subscribe;
	Element unsubscribe;
	Element div1;
	Element form;
	Element input;
	Element div2;
	Element nav;

	MatcherFilterLoader loader;

	@BeforeEach
	void setUp() {
		loader = new MatcherFilterLoader();

		html = root( "html", attributes( "lang", "en" ) );
		body = element( html, "body", id( html, "body", 1 ) );
		subscribe = element( body, "button", id( body, "button", 1, "id", "btn-subscribe" ) );
		unsubscribe = element( body, "button", id( body, "button", 2, "id", "btn-unsubscribe" ) );
		div1 = element( body, "div", id( body, "div", 1, "id", "div" ) );
		form = element( div1, "form", id( div1, "form", 1, "id", "form" ) );
		input = element( form, "input", id( form, "input", 1, "id", "input" ) );
		div2 = element( body, "div", id( body, "div", 2 ) );
		nav = element( div2, "nav", id( div2, "nav", 1 ) );
	}

	@Test
	void exclude_matcher_should_correctly_apply_to_chained_and_nested_children() {
		final String line =
				"matcher: type=body, exclude(matcher: id=btn-subscribe), exclude(matcher: xpath=html/body/div, exclude(matcher: id=form))";

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( html ) ).isFalse();
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( subscribe ) ).isFalse();
			assertThat( filter.matches( unsubscribe ) ).isTrue();
			assertThat( filter.matches( div1 ) ).isFalse();
			assertThat( filter.matches( form ) ).isTrue();
			assertThat( filter.matches( input ) ).isTrue();
			assertThat( filter.matches( div2 ) ).isTrue();
			assertThat( filter.matches( nav ) ).isTrue();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_matcher_with_attribute_should_only_match_changed_for_that_element_and_attribute_value( String key ) {
		String line = String.format( "matcher: type=body, exclude(matcher: id=form, %s=text)", key );
		final AttributeDifference text = new AttributeDifference( "text", "foo", "bar" );
		final AttributeDifference content = new AttributeDifference( "content", "foo", "bar" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, "content" ) ).isTrue();
			assertThat( filter.matches( body, content ) ).isTrue();
			assertThat( filter.matches( body, "text" ) ).isTrue();
			assertThat( filter.matches( body, text ) ).isTrue();

			assertThat( filter.matches( form ) ).isTrue();
			assertThat( filter.matches( form, "content" ) ).isTrue();
			assertThat( filter.matches( form, content ) ).isTrue();
			assertThat( filter.matches( form, "text" ) ).isFalse();
			assertThat( filter.matches( form, text ) ).isFalse();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_matcher_with_attribute_and_pixel_diff_should_only_match_element_attribute_pixel_differences(
			String key ) {
		String line = String
				.format( "matcher: type=body, exclude(matcher: id=div, %s=border-bottom-width, pixel-diff=5px)", key );

		AttributeDifference equalBelow = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		AttributeDifference equalEqual = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		AttributeDifference equalAbove = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		AttributeDifference nonEqualBelow = new AttributeDifference( "border-top-width", "1px", "2px" );
		AttributeDifference nonEqualEqual = new AttributeDifference( "border-top-width", "1px", "5px" );
		AttributeDifference nonEqualAbove = new AttributeDifference( "border-top-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( body, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( body, equalBelow ) ).isTrue();
			assertThat( filter.matches( body, equalEqual ) ).isTrue();
			assertThat( filter.matches( body, equalAbove ) ).isTrue();
			assertThat( filter.matches( body, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( body, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( body, nonEqualAbove ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( div1, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( div1, equalBelow ) ).isFalse();
			assertThat( filter.matches( div1, equalEqual ) ).isFalse();
			assertThat( filter.matches( div1, equalAbove ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualAbove ) ).isTrue();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_matcher_with_attribute_and_value_should_only_match_changed_for_that_element_and_attribute_value(
			String key ) {
		String line = String
				.format( "matcher: type=body, exclude(matcher: id=div, %s=border-bottom-width, value-regex=5px)", key );

		final AttributeDifference equalBelow = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equalEqual = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference equalAbove = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		final AttributeDifference nonEqualBelow = new AttributeDifference( "border-top-width", "1px", "2px" );
		final AttributeDifference nonEqualEqual = new AttributeDifference( "border-top-width", "1px", "5px" );
		final AttributeDifference nonEqualAbove = new AttributeDifference( "border-top-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( body, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( body, equalBelow ) ).isTrue();
			assertThat( filter.matches( body, equalEqual ) ).isTrue();
			assertThat( filter.matches( body, equalAbove ) ).isTrue();
			assertThat( filter.matches( body, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( body, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( body, nonEqualAbove ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( div1, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( div1, equalBelow ) ).isTrue();
			assertThat( filter.matches( div1, equalEqual ) ).isFalse();
			assertThat( filter.matches( div1, equalAbove ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualAbove ) ).isTrue();
		} );
	}

	@Test
	void exclude_matcher_with_pixel_diff_should_only_match_pixel_differences() {
		String line = "matcher: type=body, exclude(matcher: id=div, pixel-diff=5px)";

		final AttributeDifference below = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equal = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference above = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, below ) ).isTrue();
			assertThat( filter.matches( body, equal ) ).isTrue();
			assertThat( filter.matches( body, above ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, below ) ).isFalse();
			assertThat( filter.matches( div1, equal ) ).isFalse();
			assertThat( filter.matches( div1, above ) ).isTrue();
		} );
	}

	@Test
	void exclude_matcher_with_value_should_only_match_exact_value() {
		String line = "matcher: type=body, exclude(matcher: id=div, value-regex=5px)";

		final AttributeDifference below = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equal = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference above = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, below ) ).isTrue();
			assertThat( filter.matches( body, equal ) ).isTrue();
			assertThat( filter.matches( body, above ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, below ) ).isTrue();
			assertThat( filter.matches( div1, equal ) ).isFalse();
			assertThat( filter.matches( div1, above ) ).isTrue();
		} );
	}

	@Test
	void exclude_matcher_with_inserted_should_only_match_inserted_differences() {
		String line = "matcher: xpath=html/body/div[2], exclude(matcher: type=nav, change=inserted)";

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( div2 ) ).isTrue();
			assertThat( filter.matches( div2, Filter.ChangeType.DELETED ) ).isTrue();
			assertThat( filter.matches( div2, Filter.ChangeType.INSERTED ) ).isTrue();

			assertThat( filter.matches( nav ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.DELETED ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.INSERTED ) ).isFalse();
		} );
	}

	@Test
	void exclude_matcher_with_deleted_should_only_match_deleted_differences() {
		String line = "matcher: xpath=html/body/div[2], exclude(matcher: type=nav, change=deleted)";

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( div2 ) ).isTrue();
			assertThat( filter.matches( div2, Filter.ChangeType.INSERTED ) ).isTrue();
			assertThat( filter.matches( div2, Filter.ChangeType.DELETED ) ).isTrue();

			assertThat( filter.matches( nav ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.INSERTED ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.DELETED ) ).isFalse();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_attribute_should_just_match_changed( String key ) {
		String line = String.format( "matcher: type=form, exclude(%s=text)", key );
		final AttributeDifference text = new AttributeDifference( "text", "foo", "bar" );
		final AttributeDifference content = new AttributeDifference( "content", "foo", "bar" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( form ) ).isTrue();
			assertThat( filter.matches( form, "content" ) ).isTrue();
			assertThat( filter.matches( form, content ) ).isTrue();
			assertThat( filter.matches( form, "text" ) ).isFalse();
			assertThat( filter.matches( form, text ) ).isFalse();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_attribute_and_pixel_diff_should_match_changed( String key ) {
		String line = String.format( "matcher: type=div, exclude(%s=border-bottom-width, pixel-diff=5px)", key );

		final AttributeDifference equalBelow = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equalEqual = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference equalAbove = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		final AttributeDifference nonEqualBelow = new AttributeDifference( "border-top-width", "1px", "2px" );
		final AttributeDifference nonEqualEqual = new AttributeDifference( "border-top-width", "1px", "5px" );
		final AttributeDifference nonEqualAbove = new AttributeDifference( "border-top-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( form ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( div1, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( div1, equalBelow ) ).isFalse();
			assertThat( filter.matches( div1, equalEqual ) ).isFalse();
			assertThat( filter.matches( div1, equalAbove ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualAbove ) ).isTrue();
		} );
	}

	@ParameterizedTest
	@ValueSource( strings = { "attribute", "attribute-regex" } )
	void exclude_attribute_and_value_should_match_changed( String key ) {
		String line = String.format( "matcher: type=div, exclude(%s=border-bottom-width, value-regex=5px)", key );

		final AttributeDifference equalBelow = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equalEqual = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference equalAbove = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		final AttributeDifference nonEqualBelow = new AttributeDifference( "border-top-width", "1px", "2px" );
		final AttributeDifference nonEqualEqual = new AttributeDifference( "border-top-width", "1px", "5px" );
		final AttributeDifference nonEqualAbove = new AttributeDifference( "border-top-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( form ) ).isTrue();

			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, "border-bottom-width" ) ).isTrue();
			assertThat( filter.matches( div1, "border-top-width" ) ).isTrue();
			assertThat( filter.matches( div1, equalBelow ) ).isTrue();
			assertThat( filter.matches( div1, equalEqual ) ).isFalse();
			assertThat( filter.matches( div1, equalAbove ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualBelow ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualEqual ) ).isTrue();
			assertThat( filter.matches( div1, nonEqualAbove ) ).isTrue();
		} );
	}

	@Test
	void exclude_pixel_diff_should_only_match_changed() {
		String line = "matcher: id=div, exclude(pixel-diff=5px)";

		final AttributeDifference below = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equal = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference above = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, below ) ).isFalse();
			assertThat( filter.matches( div1, equal ) ).isFalse();
			assertThat( filter.matches( div1, above ) ).isTrue();
		} );
	}

	@Test
	void exclude_value_should_only_match_exact_value() {
		String line = "matcher: id=div, exclude(value-regex=5px)";

		final AttributeDifference below = new AttributeDifference( "border-bottom-width", "1px", "2px" );
		final AttributeDifference equal = new AttributeDifference( "border-bottom-width", "1px", "5px" );
		final AttributeDifference above = new AttributeDifference( "border-bottom-width", "1px", "7px" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( div1 ) ).isTrue();
			assertThat( filter.matches( div1, below ) ).isTrue();
			assertThat( filter.matches( div1, equal ) ).isFalse();
			assertThat( filter.matches( div1, above ) ).isTrue();
		} );
	}

	@Test
	void exclude_inserted_should_only_match_inserted_differences() {
		String line = "matcher: type=nav, exclude(change=inserted)";

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( nav ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.INSERTED ) ).isFalse();
			assertThat( filter.matches( nav, Filter.ChangeType.DELETED ) ).isTrue();
		} );
	}

	@Test
	void exclude_deleted_should_only_match_deleted_differences() {
		String line = "matcher: type=nav, exclude(change=deleted)";

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( nav ) ).isTrue();
			assertThat( filter.matches( nav, Filter.ChangeType.DELETED ) ).isFalse();
			assertThat( filter.matches( nav, Filter.ChangeType.INSERTED ) ).isTrue();
		} );
	}

	@Test
	void exclude_import_content_should_match_content() {
		String line = "matcher: type=body, exclude(import: content.filter)";
		final AttributeDifference text = new AttributeDifference( "text", "foo", "bar" );
		final AttributeDifference width = new AttributeDifference( "width", "10px", "20px" );
		final AttributeDifference placeholder = new AttributeDifference( "placeholder", "Email", "Username" );

		assertThat( loader.load( line ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( body ) ).isTrue();
			assertThat( filter.matches( body, "width" ) ).isTrue();
			assertThat( filter.matches( body, width ) ).isTrue();
			assertThat( filter.matches( body, "title" ) ).isFalse();
			assertThat( filter.matches( body, text ) ).isFalse();

			assertThat( filter.matches( input ) ).isTrue();
			assertThat( filter.matches( input, "placeholder" ) ).isFalse();
			assertThat( filter.matches( input, placeholder ) ).isFalse();
		} );
	}

	private RootElement root( final String type, final Attributes attributes ) {
		return new RootElement( type, id( type, Path.fromString( type ) ), attributes, null, null, 0, null );
	}

	private Element element( final Element parent, final String type, final IdentifyingAttributes id ) {
		return element( parent, type, id, attributes() );
	}

	private Element element( final Element parent, final String type, final IdentifyingAttributes id,
			final Attributes attributes ) {
		final Element element = Element.create( type, parent, id, attributes );
		parent.addChildren( element );
		return element;
	}

	private Attributes attributes( final String... attributes ) {
		final MutableAttributes builder = new MutableAttributes();
		for ( int i = 0; i < attributes.length; i += 2 ) {
			builder.put( attributes[i], attributes[i + 1] );
		}
		return builder.immutable();
	}

	private IdentifyingAttributes id( final Element parent, final String type, final int suffix,
			final String... attributes ) {
		return id( type, Path.path( parent.getIdentifyingAttributes().getPathTyped(), new PathElement( type, suffix ) ),
				attributes );
	}

	private IdentifyingAttributes id( final String type, final Path path, final String... attributes ) {
		return new IdentifyingAttributes( // 
				Stream.concat( // 
						Stream.of( //
								new PathAttribute( path ), //
								new StringAttribute( IdentifyingAttributes.TYPE_ATTRIBUTE_KEY, type ) //
						), //
						IntStream.range( 0, attributes.length / 2 ) //
								.mapToObj( i -> new StringAttribute( attributes[i], attributes[i + 1] ) ) // 
				).collect( Collectors.toList() ) );
	}
}
