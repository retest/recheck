package de.retest.recheck.printer.leaf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.ui.diff.DurationDifference;

class DurationDifferencePrinterTest {

	DurationDifferencePrinter cut;

	@BeforeEach
	void setUp() {
		cut = new DurationDifferencePrinter();
	}

	@Test
	void toString_should_print_difference() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getExpected() ).thenReturn( 0L );
		when( difference.getActual() ).thenReturn( 1L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "0.001 s" );
	}

	@Test
	void toString_should_respect_indent() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getExpected() ).thenReturn( 0L );
		when( difference.getActual() ).thenReturn( 1L );

		final String string = cut.toString( difference, "____" );

		assertThat( string ).startsWith( "____" );
	}
}
