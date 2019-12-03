package de.retest.recheck.meta;

import java.util.Map;

/**
 * Combines the {@link GlobalMetadataProvider} and an arbitrary local metadata provider.
 */
public final class MetadataProviderService implements MetadataProvider {

	private static final MetadataProvider GLOBAL_METADATA_PROVIDER = new GlobalMetadataProvider();

	private final MetadataProvider provider;

	MetadataProviderService( final MetadataProvider global, final MetadataProvider local ) {
		provider = MultiMetadataProvider.of( global, local );
	}

	public static MetadataProvider of( final MetadataProvider localMetadataProvider ) {
		return new MetadataProviderService( GLOBAL_METADATA_PROVIDER, localMetadataProvider );
	}

	@Override
	public Map<String, String> retrieve() {
		return provider.retrieve();
	}
}
