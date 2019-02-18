package de.retest.recheck.ignore;

import java.nio.file.Path;
import java.util.Optional;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.configuration.ProjectConfigurationUtil;

public class RecheckIgnoreUtil {

	private RecheckIgnoreUtil() {

	}

	public static Optional<Path> getIgnoreFile() {
		final Path projectConfig = ProjectConfigurationUtil.findProjectConfiguration();
		final Path ignoreFile = projectConfig.resolve( ProjectConfiguration.RECHECK_IGNORE );

		if ( ignoreFile.toFile().exists() ) {
			return Optional.of( ignoreFile );
		}

		return Optional.empty();
	}
}
