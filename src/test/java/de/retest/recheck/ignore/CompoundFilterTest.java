package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class CompoundFilterTest {
	private Element element;
	private CompoundFilter filter;
	private AttributeDifference attributeDifference;

	@BeforeEach
	void setUp() {
		element = mock( Element.class );
		attributeDifference = mock( AttributeDifference.class );
		final AttributeFilter attributeFilter = mock( AttributeFilter.class );

		when( attributeFilter.matches( element, "input" ) ).thenReturn( true );
		when( attributeFilter.matches( element, attributeDifference ) ).thenCallRealMethod();
		filter = new CompoundFilter( attributeFilter );
	}

	@Test
	void should_not_match_when_matching_element() {
		assertThat( filter.matches( element ) ).isFalse();
	}

	@Test
	void should_match_with_attribute_key_when_attribute_is_same() {
		final String attributeKey = "input";
		assertThat( filter.matches( element, attributeKey ) ).isTrue();
	}

	@Test
	void should_not_match_with_attribute_key_when_attribute_is_different() {
		final String attributeKey = "tag";
		assertThat( filter.matches( element, attributeKey ) ).isFalse();
	}

	@Test
	void should_match_with_attribute_difference_when_attribute_is_same() {
		when( attributeDifference.getKey() ).thenReturn( "input" );
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
	}

	@Test
	void should_not_match_with_attribute_difference_when_attribute_is_different() {
		when( attributeDifference.getKey() ).thenReturn( "tag" );
		assertThat( filter.matches( element, attributeDifference ) ).isFalse();
	}
}
