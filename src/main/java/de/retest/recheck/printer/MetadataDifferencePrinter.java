package de.retest.recheck.printer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.retest.recheck.meta.global.OSMetadataProvider;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;

public class MetadataDifferencePrinter implements Printer<MetadataDifference> {

	private static final String KEY_EXPECTED_ACTUAL_FORMAT = "%s: expected=\"%s\", actual=\"%s\"";

	// Only print a selected amount of differences that potentially can break a test
	private static final Set<String> DIFFERENCES_TO_PRINT = new HashSet<>( Arrays.asList( //
			OSMetadataProvider.OS_NAME, //
			OSMetadataProvider.OS_VERSION //
	) );

	@Override
	public String toString( final MetadataDifference difference, final String indent ) {
		final String prefix = "Metadata Differences:";
		return indent + prefix + printDifferences( difference, indent + "\t" );
	}

	private String printDifferences( final MetadataDifference difference, final String indent ) {
		return difference.getDifferences().stream() //
				.filter( this::shouldPrint ) //
				.map( this::print ) //
				.collect( Collectors.joining( "\n" + indent, "\n" + indent, "" ) );
	}

	private boolean shouldPrint( final MetadataElementDifference difference ) {
		return DIFFERENCES_TO_PRINT.contains( difference.getKey() );
	}

	private String print( final MetadataElementDifference difference ) {
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, difference.getKey(), difference.getExpected(),
				difference.getActual() );
	}
}
