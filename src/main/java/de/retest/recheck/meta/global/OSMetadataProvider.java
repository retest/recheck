package de.retest.recheck.meta.global;

import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.meta.MetadataProvider;

public final class OSMetadataProvider implements MetadataProvider {

	public static final String OS_ARCH = "os.arch";
	public static final String OS_NAME = "os.name";
	public static final String OS_VERSION = "os.version";

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();
		// These are required java system properties, thus they should always be set
		map.put( OS_ARCH, System.getProperty( OS_ARCH ) );
		map.put( OS_NAME, System.getProperty( OS_NAME ) );
		map.put( OS_VERSION, System.getProperty( OS_VERSION ) );
		return map;
	}
}
