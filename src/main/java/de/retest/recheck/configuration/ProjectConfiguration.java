package de.retest.recheck.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectConfiguration {

	private static final Logger logger = LoggerFactory.getLogger( ProjectConfiguration.class );
	private static final String RETEST_PROJECT_DEFAULTS = "retest-project-defaults.properties";
	private static ProjectConfiguration instance;

	public static final String RETEST_PROJECT_PROPERTIES = "retest.properties";
	public static final String RETEST_PROJECT_CONFIG_FOLDER = ".retest";

	private ProjectConfiguration() {

	}

	public static ProjectConfiguration getInstance() {
		if ( instance == null ) {
			instance = new ProjectConfiguration();
		}
		return instance;
	}

	public void ensureProjectConfigurationInitialized() {
		final Path projectRoot = ProjectRootFinderUtil.getProjectRoot();
		final Path projectConfigFolder = projectRoot.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		final Path projectConfigFile = projectConfigFolder.resolve( RETEST_PROJECT_PROPERTIES );

		createProjectConfigurationFolderIfNeeded( projectConfigFolder );
		createEmptyProjectConfigurationIfNeeded( projectConfigFile );
	}

	private void createProjectConfigurationFolderIfNeeded( final Path configFolder ) {
		if ( !configFolder.toFile().exists() ) {
			try {
				Files.createDirectories( configFolder );
				Files.copy( getClass().getClassLoader().getResourceAsStream( RETEST_PROJECT_DEFAULTS ),
						configFolder.resolve( RETEST_PROJECT_PROPERTIES ) );
				logger.info( "Creating empty project configuration in {}.", configFolder );
			} catch ( final IOException e ) {
				logger.error( "Error creating project configuration folder in {}.", configFolder );
			}
		}
	}

	private void createEmptyProjectConfigurationIfNeeded( final Path configFolder ) {
		if ( !configFolder.toFile().exists() ) {
			try {
				Files.copy( getClass().getClassLoader().getResourceAsStream( RETEST_PROJECT_DEFAULTS ), configFolder );
				logger.info( "Creating empty recheck configuration in {}.", configFolder );
			} catch ( final IOException e ) {
				logger.error( "Error creating empty recheck configuration in {}.", configFolder );
			}
		}
	}

}
