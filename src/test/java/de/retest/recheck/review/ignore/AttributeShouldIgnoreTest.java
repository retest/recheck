package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

class AttributeShouldIgnoreTest {

	@Test
	void should_match_when_attribute_is_identifying() {
		final AttributeShouldIgnore shouldIgnore = new AttributeShouldIgnore( "tag" );

		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.get( "tag" ) ).thenReturn( "div" );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "tag" );

		assertThat( shouldIgnore.matches( element, attributeDifference ) ).isTrue();
	}

	@Test
	void should_match_when_attribute_is_not_identifying() {
		final AttributeShouldIgnore shouldIgnore = new AttributeShouldIgnore( "mySpecialAttribute" );

		final Element element = mock( Element.class );
		final Attributes attribs = mock( Attributes.class );
		when( element.getAttributes() ).thenReturn( attribs );
		when( attribs.get( "mySpecialAttribute" ) ).thenReturn( "someValue" );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "mySpecialAttribute" );

		assertThat( shouldIgnore.matches( element, attributeDifference ) ).isTrue();
	}

	@Test
	void should_not_match_when_attribute_is_different() {
		final AttributeShouldIgnore shouldIgnore = new AttributeShouldIgnore( "tag" );

		final Element element = mock( Element.class );
		final IdentifyingAttributes identAttribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identAttribs );
		when( identAttribs.get( "type" ) ).thenReturn( "div" );
		final Attributes attribs = mock( Attributes.class );
		when( element.getAttributes() ).thenReturn( attribs );
		when( attribs.get( "mySpecialAttribute" ) ).thenReturn( "someValue" );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "mySpecialAttribute" );

		assertThat( shouldIgnore.matches( element, attributeDifference ) ).isFalse();
	}
}
