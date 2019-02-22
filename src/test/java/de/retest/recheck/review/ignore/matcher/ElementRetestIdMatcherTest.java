package de.retest.recheck.review.ignore.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;

class ElementRetestIdMatcherTest {

	ElementRetestIdMatcher cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		cut = new ElementRetestIdMatcher( element );
	}

	@Test
	void test_should_verify_correctly_when_id_is_equal() {
		final Element mock = mock( Element.class );
		when( mock.getRetestId() ).thenReturn( "abc" );

		assertThat( cut ).accepts( mock );
	}

	@Test
	void test_should_reject_when_id_does_not_match() {
		final Element prefix = mock( Element.class );
		when( prefix.getRetestId() ).thenReturn( "abcother" );
		final Element suffix = mock( Element.class );
		when( suffix.getRetestId() ).thenReturn( "otherabc" );
		final Element infix = mock( Element.class );
		when( infix.getRetestId() ).thenReturn( "otherabcother" );
		final Element random = mock( Element.class );
		when( random.getRetestId() ).thenReturn( "other" );

		assertThat( cut ).rejects( prefix, suffix, infix );
	}

	@Test
	void test_should_not_ignore_case() {
		final Element Abc = mock( Element.class );
		when( Abc.getRetestId() ).thenReturn( "Abc" );
		final Element aBc = mock( Element.class );
		when( aBc.getRetestId() ).thenReturn( "aBc" );
		final Element abC = mock( Element.class );
		when( abC.getRetestId() ).thenReturn( "abC" );
		final Element ABc = mock( Element.class );
		when( ABc.getRetestId() ).thenReturn( "ABc" );
		final Element AbC = mock( Element.class );
		when( AbC.getRetestId() ).thenReturn( "AbC" );
		final Element aBC = mock( Element.class );
		when( aBC.getRetestId() ).thenReturn( "aBC" );
		final Element ABC = mock( Element.class );
		when( ABC.getRetestId() ).thenReturn( "ABC" );

		assertThat( cut ).rejects( ABc, aBc, abC, ABc, Abc, aBC, ABC );
	}
}
