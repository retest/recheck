package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRootFinder {

	/**
	 * Searches for a recheck project root folder, starting from the given {@code basePath} and moving up to the root of
	 * the filesystem. This can be used to locate the project root for a given path to a report.
	 */
	Optional<Path> findProjectRoot( Path basePath );

}
