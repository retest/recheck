package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;

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

		assertThat( cut.toString( differences, "____" ) ).isEqualTo( "____Metadata Differences:\n" //
				+ "\t  Please note that these differences do not affect the result and are not included " //
				+ "in the difference count.\n" //
				+ "____\ta: expected=\"b\", actual=\"c\"\n" //
				+ "____\tb: expected=\"c\", actual=\"d\"" );
	}
}
