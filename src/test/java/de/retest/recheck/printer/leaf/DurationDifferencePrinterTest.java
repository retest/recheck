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
	void toString_should_handle_positive_difference() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getExpected() ).thenReturn( 0L );
		when( difference.getActual() ).thenReturn( 1L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "0.001 s" );
	}

	@Test
	void toString_should_handle_negative_difference() {
		final DurationDifference difference = mock( DurationDifference.class );
		when( difference.getExpected() ).thenReturn( 1L );
		when( difference.getActual() ).thenReturn( 0L );

		final String string = cut.toString( difference );

		assertThat( string ).isEqualTo( "-0.001 s" );
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
