package de.retest.recheck.ignore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.configuration.ProjectConfigurationUtil;

public class RecheckIgnoreUtil {

	private RecheckIgnoreUtil() {

	}

	public static Optional<Path> getIgnoreFile() {
		final String projectBasePath = System.getProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "" );
		if ( !projectBasePath.isEmpty() ) {
			final Path projectConfig = ProjectConfigurationUtil.findProjectConfigurationFolder(
					Paths.get( projectBasePath ) );
			final Path ignoreFile = projectConfig.resolve( ProjectConfiguration.RECHECK_IGNORE );
			if ( ignoreFile.toFile().exists() ) {
				return Optional.of( ignoreFile );
			}
		} else {
			final Path ignoreFile = ProjectConfigurationUtil.findProjectConfigurationFolder().resolve(
					ProjectConfiguration.RECHECK_IGNORE );
			if ( ignoreFile.toFile().exists() ) {
				return Optional.of( ignoreFile );
			}
		}
		return Optional.empty();
	}
}
