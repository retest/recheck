package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ElementAttributeShouldIgnoreTest {

	ElementAttributeShouldIgnore cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		cut = new ElementAttributeShouldIgnore( new ElementRetestIdMatcher( element ), "123" );
	}

	@Test
	void shouldBeFiltered_should_always_return_false() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		assertThat( cut.shouldBeFiltered( element ) ).isFalse();
	}

	@Test
	void shouldBeFiltered_should_return_true() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "123" );

		assertThat( cut.shouldBeFiltered( element, difference ) ).isTrue();
	}

	@Test
	void shouldBeFiltered_should_return_false_when_key_does_not_match() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "234" );

		assertThat( cut.shouldBeFiltered( element, difference ) ).isFalse();
	}

	@Test
	void shouldBeFiltered_should_return_false_when_element_does_not_match() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "ABC" );

		final AttributeDifference difference = mock( AttributeDifference.class );
		when( difference.getKey() ).thenReturn( "123" );

		assertThat( cut.shouldBeFiltered( element, difference ) ).isFalse();
	}
}
