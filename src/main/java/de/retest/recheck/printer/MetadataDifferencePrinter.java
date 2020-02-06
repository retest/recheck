package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;

public class MetadataDifferencePrinter implements Printer<MetadataDifference> {

	private static final String KEY_EXPECTED_ACTUAL_FORMAT = "%s: expected=\"%s\", actual=\"%s\"";

	@Override
	public String toString( final MetadataDifference difference, final String indent ) {
		final String prefix = "Metadata Differences:";
		final String note =
				"\n\t  Please note that these differences do not affect the result and are not included in the difference count.";
		return indent + prefix + note + printDifferences( difference, indent + "\t" );
	}

	private String printDifferences( final MetadataDifference difference, final String indent ) {
		return difference.getDifferences().stream() //
				.map( this::print ) //
				.collect( Collectors.joining( "\n" + indent, "\n" + indent, "" ) );
	}

	private String print( final MetadataElementDifference difference ) {
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, difference.getKey(), difference.getExpected(),
				difference.getActual() );
	}
}
