package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ElementShouldIgnoreTest {

	ElementShouldIgnore cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final ElementRetestIdMatcher matcher = new ElementRetestIdMatcher( element );
		cut = new ElementShouldIgnore( matcher );
	}

	@Test
	void shouldIgnoreElement_should_accept_element() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		assertThat( cut.filterElement( element ) ).isTrue();
	}

	@Test
	void shouldIgnoreElement_should_reject_element() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "ABC" );

		assertThat( cut.filterElement( element ) ).isFalse();
	}

	@Test
	void shouldIgnoreAttributeDifference_should_always_be_false() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final AttributeDifference difference = mock( AttributeDifference.class );

		assertThat( cut.filterAttributeDifference( element, difference ) ).isFalse();
	}
}
