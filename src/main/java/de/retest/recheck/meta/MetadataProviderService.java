package de.retest.recheck.meta;

/**
 * Combines the {@link GlobalMetadataProvider} and an arbitrary local metadata provider.
 */
public final class MetadataProviderService {

	private static final MetadataProvider GLOBAL_METADATA_PROVIDER = new GlobalMetadataProvider();

	public static MetadataProvider of( final MetadataProvider localMetadataProvider ) {
		return MultiMetadataProvider.of( GLOBAL_METADATA_PROVIDER, localMetadataProvider );
	}
}
