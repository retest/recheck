package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher.ElementIdMatcherLoader;

public class ElementIdMatcherLoaderTest {

	ElementIdMatcher matcher;
	ElementIdMatcherLoader cut;

	@BeforeEach
	void setUp() {
		cut = new ElementIdMatcherLoader();

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		matcher = new ElementIdMatcher( element );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( matcher ) ).isEqualTo( "id=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "id=abc";
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}
}
