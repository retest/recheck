package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementIdMatcherTest {

	ElementIdMatcher cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.get( "id" ) ).thenReturn( "someDiv" );
		cut = new ElementIdMatcher( element );
	}

	@Test
	void should_match_when_id_is_equal() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.get( "id" ) ).thenReturn( "someDiv" );

		assertThat( cut ).accepts( element );
	}

	@Test
	void should_not_match_when_id_is_not_equal() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.get( "id" ) ).thenReturn( "otherDiv" );

		assertThat( cut ).rejects( element );
	}
}
