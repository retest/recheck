package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;

class ElementTypeMatcherTest {

	private ElementTypeMatcher matcher;

	@BeforeEach
	void setUp() {
		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "html/meta" ), "meta" ),
				new MutableAttributes().immutable() );

		matcher = new ElementTypeMatcher( element );
	}

	@Test
	void should_match_when_type_is_equal() {
		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "html/meta" ), "meta" ),
				new MutableAttributes().immutable() );

		assertThat( matcher ).accepts( element );
	}

	@Test
	void should_not_match_when_type_is_not_equal() {
		final Element element = Element.create( "retestId", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "html/div" ), "div" ),
				new MutableAttributes().immutable() );

		assertThat( matcher ).rejects( element );
	}
}
