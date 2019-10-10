package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementClassMatcher.ElementClassMatcherLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class ElementClassMatcherLoaderTest {

	ElementClassMatcher matcher;
	ElementClassMatcherLoader cut;

	@BeforeEach
	void setUp() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( ElementClassMatcher.CLASS_KEY ) ).thenReturn( "some-class" );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		matcher = new ElementClassMatcher( element );

		cut = new ElementClassMatcherLoader();
	}

	@Test
	void save_should_produce_correct_line() {
		final String line = "class=some-class";
		assertThat( cut.save( matcher ) ).isEqualTo( line );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "class=some-class";
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}
}
