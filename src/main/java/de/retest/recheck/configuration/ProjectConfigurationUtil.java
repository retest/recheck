package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectConfigurationUtil {

	private ProjectConfigurationUtil() {}

	public static Optional<Path> findProjectConfigurationFolder() {
		return ProjectRootFinderUtil.getProjectRoot()
				.map( path -> path.resolve( ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER ) );
	}

	public static Optional<Path> findProjectConfigurationFolder( final Path basePath ) {
		return ProjectRootFinderUtil.getProjectRoot( basePath )
				.map( path -> path.resolve( ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER ) );
	}
}
