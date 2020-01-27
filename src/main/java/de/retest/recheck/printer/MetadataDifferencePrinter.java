package de.retest.recheck.printer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.retest.recheck.meta.global.GitMetadataProvider;
import de.retest.recheck.meta.global.MachineMetadataProvider;
import de.retest.recheck.meta.global.OSMetadataProvider;
import de.retest.recheck.meta.global.TimeMetadataProvider;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;

public class MetadataDifferencePrinter implements Printer<MetadataDifference> {

	private static final String KEY_EXPECTED_ACTUAL_FORMAT = "%s: expected=\"%s\", actual=\"%s\"";

	// Only print a selected amount of differences that potentially can break a test
	private static final Set<String> differencesToIgnore = new HashSet<>( Arrays.asList( //
			GitMetadataProvider.VCS_NAME, //
			GitMetadataProvider.BRANCH_NAME, //
			GitMetadataProvider.COMMIT_NAME, //
			MachineMetadataProvider.MACHINE_NAME, //
			OSMetadataProvider.OS_ARCH, //
			TimeMetadataProvider.DATE, //
			TimeMetadataProvider.TIME, //
			TimeMetadataProvider.ZONE, //
			TimeMetadataProvider.OFFSET //
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
		return !differencesToIgnore.contains( difference.getKey() );
	}

	private String print( final MetadataElementDifference difference ) {
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, difference.getKey(), difference.getExpected(),
				difference.getActual() );
	}
}
