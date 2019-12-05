package de.retest.recheck.meta.global;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import de.retest.recheck.meta.MetadataProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MachineMetadataProvider implements MetadataProvider {

	public static final String MACHINE_NAME = "machine.name";

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();

		final String hostName = SystemUtils.getHostName();
		if ( hostName != null ) {
			map.put( MACHINE_NAME, hostName );
		}

		return map;
	}
}
