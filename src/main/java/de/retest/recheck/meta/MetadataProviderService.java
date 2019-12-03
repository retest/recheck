package de.retest.recheck.meta;

import java.util.Map;

/**
 * <p>
 * Combines the {@link GlobalMetadataProvider} and an arbitrary local metadata provider.
 * 
 * <p>
 * This allows for duplicate keys by preferring the local metadata over the global. That means that the local provider
 * is capable of overwriting metadata from the global provider.
 */
public final class MetadataProviderService implements MetadataProvider {

	private static final MetadataProvider GLOBAL_METADATA_PROVIDER = new GlobalMetadataProvider();

	private final MetadataProvider provider;

	MetadataProviderService( final MetadataProvider global, final MetadataProvider local ) {
		provider = MultiMetadataProvider.of( global, local );
	}

	/**
	 * Constructs a merging provider, using the {@link GlobalMetadataProvider} and the provided local provider. The
	 * local provider is able to overwrite keys from the global provider.
	 * 
	 * @param localMetadataProvider
	 *            The local provider.
	 * @return The merging provider.
	 */
	public static MetadataProvider of( final MetadataProvider localMetadataProvider ) {
		return new MetadataProviderService( GLOBAL_METADATA_PROVIDER, localMetadataProvider );
	}

	@Override
	public Map<String, String> retrieve() {
		return provider.retrieve();
	}
}
