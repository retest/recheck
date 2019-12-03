package de.retest.recheck.meta.global;

import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.meta.MetadataProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MachineMetadataProvider implements MetadataProvider {

	private static final String WINDOWS_COMPUTER_NAME = "COMPUTERNAME";
	private static final String UNIX_HOST_NAME = "HOSTNAME";

	public static final String MACHINE_NAME = "machine.name";

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();

		final String machineName = retrieveMachineName();
		if ( machineName != null ) {
			map.put( MACHINE_NAME, machineName );
		}

		return map;
	}

	// Refer to https://stackoverflow.com/a/33112997 and https://stackoverflow.com/a/40702767
	private String retrieveMachineName() {
		final String computerName = System.getenv( WINDOWS_COMPUTER_NAME );
		if ( computerName != null ) {
			return computerName;
		}
		final String hostname = System.getenv( UNIX_HOST_NAME );
		if ( hostname != null ) {
			return hostname;
		}
		return null;
	}
}
