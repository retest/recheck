package de.retest.recheck.printer.leaf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.diff.DurationDifference;
import de.retest.recheck.ui.diff.LeafDifference;

class MultiLeafDifferencePrinterTest {

	MultiLeafDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new MultiLeafDifferencePrinter( ( key, value ) -> false );
	}

	@Test
	void toString_should_use_corresponding_printer_for_known_difference() {
		final DurationDifference difference = DurationDifference.differenceFor( 1000L, 2000L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "1.000 s" );
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
