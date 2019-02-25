package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectConfigurationUtil {

	private ProjectConfigurationUtil() {}

	public static Optional<Path> findProjectConfiguration() {
		return ProjectRootFinderUtil.getProjectRoot().map( ProjectConfigurationUtil::resolveProperties );
	}

	public static Optional<Path> findProjectConfiguration( final Path path ) {
		return ProjectRootFinderUtil.getProjectRoot( path ).map( ProjectConfigurationUtil::resolveProperties );
	}

	private static Path resolveProperties( final Path path ) {
		return path.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES );
	}
}
