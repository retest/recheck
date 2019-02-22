package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectConfigurationUtil {

	private ProjectConfigurationUtil() {

	}

	public static Optional<Path> findProjectConfiguration() {
		final Optional<Path> projectConfigurationFolder = ProjectRootFinderUtil.getProjectRoot();
		return findProjectConfiguration( projectConfigurationFolder );
	}

	public static Optional<Path> findProjectConfiguration( final Path path ) {
		final Optional<Path> projectConfigurationFolder = ProjectRootFinderUtil.getProjectRoot( path );
		return findProjectConfiguration( projectConfigurationFolder );
	}

	private static Optional<Path> findProjectConfiguration( final Optional<Path> projectConfigurationFolder ) {
		if ( projectConfigurationFolder.isPresent() ) {
			return Optional.of( projectConfigurationFolder.get() //
					.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES ) );
		}
		return Optional.empty();
	}
}
