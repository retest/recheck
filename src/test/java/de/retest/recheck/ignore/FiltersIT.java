package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class FiltersIT {

	private Element element;
	private Element childElement;
	private AttributeDifference attributeDifference;

	@BeforeEach
	void setUp() {
		element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.getType() ).thenReturn( "input" );

		childElement = mock( Element.class );
		final IdentifyingAttributes childAttribs = mock( IdentifyingAttributes.class );
		when( childElement.getIdentifyingAttributes() ).thenReturn( childAttribs );
		when( childAttribs.getType() ).thenReturn( "output" );

		when( childElement.getParent() ).thenReturn( element );

		attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "outline" );
	}

	@Test
	void matches_should_work_for_children() {
		final Filter filter = Filters.parse( "matcher: type=input" );
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement ) ).isTrue();
	}

	@Test
	void matches_should_work_for_children_when_searching_attribute() {
		final Filter filter = Filters.parse( "matcher: type=input, attribute: outline" );
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement ) ).isFalse();
	}

	@Test
	void matches_should_work_for_children_when_searching_regex() {
		final Filter filter = Filters.parse( "matcher: type=input, attribute-regex: .*" );
		assertThat( filter.matches( element, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement, attributeDifference ) ).isTrue();
		assertThat( filter.matches( childElement ) ).isFalse();
	}
}
