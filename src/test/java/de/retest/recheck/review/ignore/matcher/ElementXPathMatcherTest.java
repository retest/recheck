package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementXPathMatcherTest {

	private ElementXPathMatcher matcher;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.getPath() ).thenReturn( "html[1]/div[1]" );
		matcher = new ElementXPathMatcher( element );
	}

	@Test
	void should_match_when_xpath_is_equal() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.getPath() ).thenReturn( "html[1]/div[1]" );

		assertThat( matcher ).accepts( element );
	}

	@Test
	void should_not_match_when_xpath_is_not_equal() {
		final Element element = mock( Element.class );
		final IdentifyingAttributes attribs = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( attribs );
		when( attribs.getPath() ).thenReturn( "html[1]/div[2]" );

		assertThat( matcher ).rejects( element );
	}
}
