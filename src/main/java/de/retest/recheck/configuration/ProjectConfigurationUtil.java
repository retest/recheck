package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectConfigurationUtil {

	private ProjectConfigurationUtil() {

	}

	public static Optional<Path> findProjectConfiguration() {
		final Optional<Path> projectConfigurationFolder = ProjectRootFinderUtil.getProjectRoot();
		return projectConfigurationFolder.isPresent()
				? projectConfigurationFolder
						.map( path -> path.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES ) )
				: projectConfigurationFolder;
	}

	public static Optional<Path> findProjectConfiguration( final Path path ) {
		final Optional<Path> projectConfigurationFolder = ProjectRootFinderUtil.getProjectRoot( path );
		return projectConfigurationFolder.isPresent()
				? projectConfigurationFolder
						.map( path1 -> path1.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES ) )
				: projectConfigurationFolder;
	}

}
