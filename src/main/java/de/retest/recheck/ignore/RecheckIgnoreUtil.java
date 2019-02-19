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
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE );
	}

	public static Optional<Path> getIgnoreRuleFile() {
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE_JSRULES );
	}

	private static Optional<Path> getRetestFile( final String filename ) {
		final String projectBasePath = System.getProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "" );
		if ( !projectBasePath.isEmpty() ) {
			final Path projectConfig =
					ProjectConfigurationUtil.findProjectConfigurationFolder( Paths.get( projectBasePath ) );
			final Path ignoreFile = projectConfig.resolve( filename );
			if ( ignoreFile.toFile().exists() ) {
				return Optional.of( ignoreFile );
			}
		} else {
			final Path ignoreFile = ProjectConfigurationUtil.findProjectConfigurationFolder().resolve( filename );
			if ( ignoreFile.toFile().exists() ) {
				return Optional.of( ignoreFile );
			}
		}
		return Optional.empty();
	}
}
