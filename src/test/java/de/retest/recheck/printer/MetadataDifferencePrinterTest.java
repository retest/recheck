package de.retest.recheck.printer;

import java.util.Arrays;
import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;

class MetadataDifferencePrinterTest {

	@Test
	void toString_should_properly_format_differences() throws Exception {
		final MetadataDifference differences = MetadataDifference.of( new HashSet<>( Arrays.asList( //
				new MetadataElementDifference( "a", "b", "c" ), //
				new MetadataElementDifference( "b", "c", "d" ) //
		) ) );

		final MetadataDifferencePrinter cut = new MetadataDifferencePrinter();

		Assertions.assertThat( cut.toString( differences, "____" ) ).isEqualTo( "____Metadata Differences:\n" //
				+ "____\ta: expected=\"b\", actual=\"c\"\n" //
				+ "____\tb: expected=\"c\", actual=\"d\"" );
	}
}
