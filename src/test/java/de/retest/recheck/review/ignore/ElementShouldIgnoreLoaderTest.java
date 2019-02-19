package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.review.ignore.ElementShouldIgnore.ElementShouldIgnoreLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;

class ElementShouldIgnoreLoaderTest {

	ElementShouldIgnoreLoader cut;
	ElementShouldIgnore ignore;

	@BeforeEach
	void setUp() {
		cut = new ElementShouldIgnoreLoader();

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final ElementIdMatcher matcher = new ElementIdMatcher( element );
		ignore = new ElementShouldIgnore( matcher );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: id=abc";
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}
}
