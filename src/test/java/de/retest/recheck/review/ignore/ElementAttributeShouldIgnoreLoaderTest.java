package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.review.ignore.ElementAttributeShouldIgnore.ElementAttributeShouldIgnoreLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;

public class ElementAttributeShouldIgnoreLoaderTest {

	ElementAttributeShouldIgnoreLoader cut;
	ElementAttributeShouldIgnore ignore;

	@BeforeEach
	void setUp() {
		cut = new ElementAttributeShouldIgnoreLoader();

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		ignore = new ElementAttributeShouldIgnore( new ElementIdMatcher( element ), "123" );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( ignore ) ).isEqualTo( "matcher: id=abc, key: 123" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "matcher: id=abc, key: 123";
		assertThat( cut.save( cut.load( line ) ) ).isEqualTo( line );
	}
}
