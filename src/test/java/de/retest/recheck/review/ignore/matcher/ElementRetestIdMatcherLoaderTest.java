package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher.ElementRetestIdMatcherLoader;
import de.retest.recheck.ui.descriptors.Element;

class ElementRetestIdMatcherLoaderTest {

	ElementRetestIdMatcher matcher;
	ElementRetestIdMatcherLoader cut;

	@BeforeEach
	void setUp() {
		cut = new ElementRetestIdMatcherLoader();

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		matcher = new ElementRetestIdMatcher( element );
	}

	@Test
	void save_should_produce_correct_line() {
		assertThat( cut.save( matcher ) ).isEqualTo( "retestid=abc" );
	}

	@Test
	void load_should_produce_correct_ignore() {
		final String line = "retestid=abc";
		assertThat( cut.load( line ).map( cut::save ) ).hasValue( line );
	}
}
