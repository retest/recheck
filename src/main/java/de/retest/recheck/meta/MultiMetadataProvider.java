package de.retest.recheck.meta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Combines multiple {@link MetadataProvider} together into a single metadata. This may be used to combine multiple
 * smaller, specified providers together or simply collect a list of unrelated providers.
 * 
 * Key collisions will be resolved with the last value encountered.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class MultiMetadataProvider implements MetadataProvider {

	private final List<MetadataProvider> providers;

	public static MetadataProvider of( final MetadataProvider... providers ) {
		return of( Arrays.asList( providers ) );
	}

	public static MetadataProvider of( final List<MetadataProvider> providers ) {
		return new MultiMetadataProvider( providers );
	}

	@Override
	public Map<String, String> retrieve() {
		return providers.stream() //
				.map( MetadataProvider::retrieve ) //
				.map( Map::entrySet ) //
				.flatMap( Set::stream ) //
				.collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( left, right ) -> right ) );
	}
}
