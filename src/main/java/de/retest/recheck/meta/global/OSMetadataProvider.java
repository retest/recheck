package de.retest.recheck.meta.global;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import de.retest.recheck.meta.MetadataProvider;

public final class OSMetadataProvider implements MetadataProvider {

	public static final String OS_ARCH = "os.arch";
	public static final String OS_NAME = "os.name";
	public static final String OS_VERSION = "os.version";

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();
		// These are required java system properties, thus they should always be set
		map.put( OS_ARCH, SystemUtils.OS_ARCH );
		map.put( OS_NAME, SystemUtils.OS_NAME );
		map.put( OS_VERSION, SystemUtils.OS_VERSION );
		return map;
	}
}
