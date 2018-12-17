package de.retest.recheck.printer.leaf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.ui.descriptors.AttributeDifference;
import de.retest.ui.diff.DurationDifference;
import de.retest.ui.diff.IdentifyingAttributesDifference;
import de.retest.ui.diff.LeafDifference;

class MultiLeafDifferencePrinterTest {

	MultiLeafDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new MultiLeafDifferencePrinter( ( key, value ) -> false );
	}

	@Test
	void toString_should_find_correct_printer_for_negative_duration_difference() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getActual() ).thenReturn( 2L );
		when( difference.getExpected() ).thenReturn( 5L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "-0.003 s" );
	}

	@Test
	void toString_should_find_correct_printer_for_positive_duration_difference() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getActual() ).thenReturn( 5L );
		when( difference.getExpected() ).thenReturn( 2L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "0.003 s" );
	}

	@Test
	void toString_should_find_correct_printer_for_identification_difference() {
		final IdentifyingAttributesDifference difference = mock( IdentifyingAttributesDifference.class );
		when( difference.getAttributes() )
				.thenReturn( Collections.singletonList( new AttributeDifference( "path", "foo", "bar" ) ) );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "path: expected=\"foo\", actual=\"bar\"" );
	}

	@Test
	void toString_should_use_default_printer_for_unknown_difference() {
		final LeafDifference difference = mock( LeafDifference.class );
		when( difference.getActual() ).thenReturn( 0L );
		when( difference.getExpected() ).thenReturn( 1L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "expected=1, actual=0" );
	}
}
