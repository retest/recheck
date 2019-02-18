package de.retest.recheck.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectConfiguration {

	private static final Logger logger = LoggerFactory.getLogger( ProjectConfiguration.class );
	private static final String RETEST_PROJECT_DEFAULTS = "retest-project-defaults.properties";
	private static final String RECHECK_IGNORE_DEFAULTS = "recheck-project-defaults.ignore";

	private static ProjectConfiguration instance;

	public static final String RETEST_PROJECT_CONFIG_FOLDER = ".retest";
	public static final String RETEST_PROJECT_PROPERTIES = "retest.properties";
	public static final String RECHECK_IGNORE = "recheck.ignore";

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
		final Path projectIgnoreFile = projectConfigFolder.resolve( RECHECK_IGNORE );

		createProjectConfigurationFolderIfNeeded( projectConfigFolder );
		createEmptyProjectConfigurationIfNeeded( projectConfigFile, RETEST_PROJECT_DEFAULTS );
		createEmptyProjectConfigurationIfNeeded( projectIgnoreFile, RECHECK_IGNORE_DEFAULTS );
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

	private void createEmptyProjectConfigurationIfNeeded( final Path configFile, final String defaultFile ) {
		if ( !configFile.toFile().exists() ) {
			try ( InputStream is = getInputStreamFrom( defaultFile ) ) {
				Files.copy( is, configFile );
				logger.info( "Creating empty recheck configuration in {}.", configFile );
			} catch ( final IOException e ) {
				logger.error( "Error creating empty recheck configuration in {}.", configFile );
			}
		}
	}

	private InputStream getInputStreamFrom( final String fileName ) {
		return getClass().getClassLoader().getResourceAsStream( fileName );
	}

}
