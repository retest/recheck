package de.retest.recheck.meta;

import java.util.Map;

import de.retest.recheck.meta.global.GitMetadataProvider;
import de.retest.recheck.meta.global.MachineMetadataProvider;
import de.retest.recheck.meta.global.OSMetadataProvider;
import de.retest.recheck.meta.global.TimeMetadataProvider;

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

	private final MetadataProvider globalProvider = MultiMetadataProvider.of( //
			new GitMetadataProvider(), //
			new MachineMetadataProvider(), //
			new OSMetadataProvider(), //
			new TimeMetadataProvider() //
	);

	@Override
	public Map<String, String> retrieve() {
		return globalProvider.retrieve();
	}
}
