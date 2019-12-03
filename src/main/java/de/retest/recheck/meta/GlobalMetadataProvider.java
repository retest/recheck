package de.retest.recheck.meta;

import java.util.Map;

/**
 * Provides generic metadata that apply in particular to the current context in which a program is executed. This
 * includes for example:
 * <ul>
 * <li>The operating systems name and version.</li>
 * <li>The date and time executed.</li>
 * <li>...</li>
 * </ul>
 */
final class GlobalMetadataProvider implements MetadataProvider {

	private final MetadataProvider globalProvider = MultiMetadataProvider.of(
	// TODO RET-1898 insert the global metadata providers here
	);

	@Override
	public Map<String, String> retrieve() {
		return globalProvider.retrieve();
	}
}
