package de.retest.recheck.printer.leaf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.ui.descriptors.AttributeDifference;
import de.retest.ui.diff.IdentifyingAttributesDifference;

class IdentifyingAttributesDifferencePrinterTest {

	IdentifyingAttributesDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new IdentifyingAttributesDifferencePrinter( ( key, value ) -> false );
	}

	@Test
	void toString_should_be_empty_when_no_differences() {
		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( difference );

		assertThat( string ).isEmpty();
	}

	@Test
	void toString_should_not_print_indent_if_empty() {
		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).isEmpty();
	}

	@Test
	void toString_should_print_differences_if_non_empty() {
		final AttributeDifference d1 = new AttributeDifference( "a", "a", "b" );

		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Collections.singletonList( d1 ) );

		final String string = cut.toString( difference );

		assertThat( string ).isNotEmpty();
	}

	@Test
	void toString_should_respect_indent() {
		final AttributeDifference d1 = new AttributeDifference( "a", "a", "b" );

		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Collections.singletonList( d1 ) );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_each_difference_in_a_new_line() {
		final AttributeDifference d1 = new AttributeDifference( "a", "a", "b" );

		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Arrays.asList( d1, d1 ) );

		final String string = cut.toString( difference );

		assertThat( string ).contains( "\n" );
	}

	@Test
	void toString_should_respect_multiple_indents() {
		final AttributeDifference d1 = new AttributeDifference( "a", "a", "b" );

		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() ).thenReturn( Arrays.asList( d1, d1 ) );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
		assertThat( string ).contains( "\n____" );
	}
}
