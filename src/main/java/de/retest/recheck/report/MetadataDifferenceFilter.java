package de.retest.recheck.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataDifferenceFilter {

	private static final Element DUMMY_ELEMENT = new RootElement( "recheck-metadata",
			IdentifyingAttributes.create( Path.fromString( "recheck/metadata" ), "metadata" ), new Attributes(), null,
			"0", 0, "0" );

	public MetadataDifference filter( final MetadataDifference metadataDifference, final Filter filter ) {
		return metadataDifference.getDifferences().stream() //
				.filter( diff -> !filter( filter, diff ) ) //
				.collect( collectingAndThen( toSet(), MetadataDifference::of ) );
	}

	private boolean filter( final Filter filter, final MetadataElementDifference diff ) {
		return filter.matches( DUMMY_ELEMENT, diff.getKey() ) // 
				|| filter.matches( DUMMY_ELEMENT, toAttributeDifference( diff ) );
	}

	private AttributeDifference toAttributeDifference( final MetadataElementDifference diff ) {
		return new AttributeDifference( diff.getKey(), diff.getExpected(), diff.getActual() );
	}
}
