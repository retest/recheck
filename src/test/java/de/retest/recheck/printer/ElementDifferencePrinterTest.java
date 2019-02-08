package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementDifference;

class ElementDifferencePrinterTest {

	ElementDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new ElementDifferencePrinter( ( identifyingAttributes, attributeKey, attributeValue ) -> false );
	}

	@Test
	void toString_should_print_child_differences() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "id" );

		final ElementDifference d1 = mock( ElementDifference.class );
		when( d1.getElementDifferences() ).thenReturn( Collections.emptyList() );
		when( d1.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final ElementDifference difference = mock( ElementDifference.class );
		when( difference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( difference.getElementDifferences() ).thenReturn( Collections.singletonList( d1 ) );

		final String string = cut.toString( difference );

		assertThat( string ).isNotEmpty();
	}

	@Test
	void toString_should_respect_indent() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "id" );

		final ElementDifference d1 = mock( ElementDifference.class );
		when( d1.getElementDifferences() ).thenReturn( Collections.emptyList() );
		when( d1.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final ElementDifference difference = mock( ElementDifference.class );
		when( difference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( difference.getElementDifferences() ).thenReturn( Collections.singletonList( d1 ) );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_each_differences_in_a_new_line() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "id" );

		final ElementDifference d1 = mock( ElementDifference.class );
		when( d1.getElementDifferences() ).thenReturn( Collections.emptyList() );
		when( d1.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final ElementDifference difference = mock( ElementDifference.class );
		when( difference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( difference.getElementDifferences() ).thenReturn( Arrays.asList( d1, d1 ) );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "\n" );
	}

	@Test
	void toString_should_respect_multiple_indent() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "id" );

		final ElementDifference d1 = mock( ElementDifference.class );
		when( d1.getElementDifferences() ).thenReturn( Collections.emptyList() );
		when( d1.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final ElementDifference difference = mock( ElementDifference.class );
		when( difference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( difference.getElementDifferences() ).thenReturn( Arrays.asList( d1, d1 ) );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
		assertThat( string ).contains( "\n____" );
	}
}
