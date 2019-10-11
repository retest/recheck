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

	String classValue;
	ElementClassMatcher matcher;
	ElementClassMatcherLoader cut;

	@BeforeEach
	void setUp() {
		classValue = "some-class";
		matcher = new ElementClassMatcher( mockElementWithClassValue( classValue ) );
		cut = new ElementClassMatcherLoader();
	}

	@Test
	void save_should_produce_correct_line_for_single_class_value() {
		assertThat( cut.save( matcher ) ).isEqualTo( toLine( classValue ) );
	}

	@Test
	void load_should_produce_correct_ignore_for_single_class_value() {
		final String line = toLine( classValue );
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}

	@Test
	void save_should_produce_correct_line_for_multiple_class_values() {
		final String classValue = "one two";
		final ElementClassMatcher matcher = new ElementClassMatcher( mockElementWithClassValue( classValue ) );
		assertThat( cut.save( matcher ) ).isEqualTo( toLine( classValue ) );
	}

	@Test
	void load_should_produce_correct_ignore_for_multiple_class_values() {
		final String line = toLine( "one two" );
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}

	private String toLine( final String classValue ) {
		return ElementClassMatcher.CLASS_KEY + "=" + classValue;
	}

	private Element mockElementWithClassValue( final String classValue ) {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.get( ElementClassMatcher.CLASS_KEY ) ).thenReturn( classValue );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		return element;
	}

}
