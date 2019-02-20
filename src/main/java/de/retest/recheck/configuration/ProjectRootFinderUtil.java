package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

public class ProjectRootFinderUtil {

	private static final Set<ProjectRootFinder> projectRootFinder = Sets.newHashSet( new MavenProjectRootFinder() );

	private ProjectRootFinderUtil() {

	}

	public static Optional<Path> getProjectRoot() {
		final Path baseFolder = Paths.get( System.getProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "" ) );

		return getProjectRoot( baseFolder.toAbsolutePath() );
	}

	public static Optional<Path> getProjectRoot( final Path basePath ) {
		return projectRootFinder.stream() //
				.map( finder -> finder.findProjectRoot( basePath ) ) //
				.filter( Optional::isPresent ) //
				.map( Optional::get ) //
				.findAny();
	}

}
