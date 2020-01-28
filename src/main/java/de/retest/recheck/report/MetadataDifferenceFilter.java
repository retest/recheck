package de.retest.recheck.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.retest.recheck.meta.global.GitMetadataProvider;
import de.retest.recheck.meta.global.MachineMetadataProvider;
import de.retest.recheck.meta.global.OSMetadataProvider;
import de.retest.recheck.meta.global.TimeMetadataProvider;
import de.retest.recheck.ui.diff.meta.MetadataDifference;

public class MetadataDifferenceFilter {

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

	public MetadataDifference filter( final MetadataDifference metadataDifference ) {
		return metadataDifference.getDifferences().stream() //
				.filter( diff -> !differencesToIgnore.contains( diff.getKey() ) ) //
				.collect( collectingAndThen( toSet(), MetadataDifference::of ) );
	}

}
