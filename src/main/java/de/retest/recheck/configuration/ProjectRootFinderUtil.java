package de.retest.recheck.configuration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

public class ProjectRootFinderUtil {

	private static Set<ProjectRootFinder> projectRootFinder = Sets.newHashSet( new MavenProjectRootFinder() );

	private ProjectRootFinderUtil() {

	}

	public static Path getProjectRoot() {
		return projectRootFinder.stream() //
				.map( ProjectRootFinder::findProjectRoot ) //
				.filter( Optional::isPresent ) //
				.map( Optional::get ) //
				.findAny() //
				.orElseThrow( () -> new UncheckedIOException(
						new IOException( "Project root not found in current workdir or any parent folder." ) ) );
	}

	public static Path getProjectRoot( final Path basePath ) {
		return projectRootFinder.stream() //
				.map( finder -> finder.findProjectRoot( basePath ) ) //
				.filter( Optional::isPresent ) //
				.map( Optional::get ) //
				.findAny() //
				.orElseThrow( () -> new UncheckedIOException(
						new IOException( "Project root not found in " + basePath + " or any parent folder." ) ) );
	}

}
