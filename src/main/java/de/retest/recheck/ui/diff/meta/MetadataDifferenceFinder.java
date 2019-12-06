package de.retest.recheck.ui.diff.meta;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.ui.descriptors.SutState;

public class MetadataDifferenceFinder {

	public MetadataDifference findDifferences( final SutState expected, final SutState actual ) {
		return findDifferences( expected.getMetadata(), actual.getMetadata() );
	}

	private MetadataDifference findDifferences( final Map<String, String> expected, final Map<String, String> actual ) {
		return collectAllKeys( expected, actual ) //
				.map( key -> differenceFor( key, expected.get( key ), actual.get( key ) ) ) //
				.filter( Objects::nonNull ) //
				.collect( Collectors.collectingAndThen( Collectors.toSet(), MetadataDifference::of ) );
	}

	private Stream<String> collectAllKeys( final Map<String, String> expected, final Map<String, String> actual ) {
		return Stream.of( expected.keySet(), actual.keySet() ) //
				.flatMap( Set::stream ) //
				.distinct();
	}

	private MetadataElementDifference differenceFor( final String key, final String expected, final String actual ) {
		return Objects.equals( expected, actual ) ? null : new MetadataElementDifference( key, expected, actual );
	}
}
