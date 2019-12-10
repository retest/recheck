package de.retest.recheck.configuration;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectConfigurationUtil {

	private ProjectConfigurationUtil() {}

	public static Optional<Path> findProjectConfigurationFolder() {
		return ProjectRootFinderUtil.getProjectRoot().map( path -> path.resolve( RETEST_FOLDER_NAME ) );
	}
}
