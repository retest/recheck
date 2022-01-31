package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.MatcherFilter;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class AllMatchFilterTest {

	Filter cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		cut = new AllMatchFilter( new MatcherFilter( new ElementRetestIdMatcher( element ) ),
				new AttributeFilter( "123" ) );
	}

	@Test
	void not_matching_all_should_return_false() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		assertThat( cut.matches( element ) ).isFalse();
	}

	@Test
	void matching_all_should_return_true() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "123" );

		assertThat( cut.matches( element, difference ) ).isTrue();
	}

	@Test
	void should_return_false_when_key_does_not_match() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "234" );

		assertThat( cut.matches( element, difference ) ).isFalse();
	}

	@Test
	void should_return_false_when_element_does_not_match() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "ABC" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "123" );

		assertThat( cut.matches( element, difference ) ).isFalse();
	}

	@Test
	void should_match_with_attribute_key_when_attribute_is_same() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final String attributeKey = "123";

		assertThat( cut.matches( element, attributeKey ) ).isTrue();
	}

	@Test
	void should_not_match_with_attribute_key_when_attribute_is_different() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final String attributeKey = "234";

		assertThat( cut.matches( element, attributeKey ) ).isFalse();
	}

	@Test
	void should_match_if_parent_matches() {
		final Element parent = mock( Element.class );
		when( parent.getRetestId() ).thenReturn( "abc" );
		final Element element = mock( Element.class );
		when( element.getParent() ).thenReturn( parent );
		when( element.getRetestId() ).thenReturn( "some other" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "123" );

		assertThat( cut.matches( parent, difference ) ).isTrue();
		assertThat( cut.matches( element, difference ) ).isTrue();
	}

	@Test
	void no_filters_given_should_throw_exception() {
		assertThatThrownBy( AllMatchFilter::new ).isInstanceOf( IllegalArgumentException.class );
	}

}
