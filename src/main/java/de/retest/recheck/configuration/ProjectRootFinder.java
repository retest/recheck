package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRootFinder {

	/**
	 * Searches for a recheck project root folder, starting from the given {@code basePath} and moving up to the root of
	 * the filesystem. This can be used to locate the project root for a given path to a report.
	 *
	 * @param basePath
	 *            the path to start the search from
	 * @return an optional consisting of the project root path if it has been found, otherwise empty
	 */
	Optional<Path> findProjectRoot( Path basePath );

}
