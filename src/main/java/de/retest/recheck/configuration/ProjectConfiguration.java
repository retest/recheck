package de.retest.recheck.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectConfiguration {

	private static final Logger logger = LoggerFactory.getLogger( ProjectConfiguration.class );

	public static final String RETEST_PROJECT_CONFIG_FOLDER = ".retest";
	public static final String RETEST_PROJECT_PROPERTIES = "retest.properties";
	public static final String RECHECK_IGNORE = "recheck.ignore";
	public static final String RECHECK_IGNORE_JSRULES = "recheck.ignore.js";
	public static final String FILTER_FOLDER = "filter";

	private static final String DEFAULT_PREFIX = "default-";
	private static final String RETEST_PROJECT_DEFAULTS = DEFAULT_PREFIX + RETEST_PROJECT_PROPERTIES;
	private static final String RECHECK_IGNORE_DEFAULTS = DEFAULT_PREFIX + RECHECK_IGNORE;
	private static final String RECHECK_IGNORE_JSRULES_DEFAULTS = DEFAULT_PREFIX + RECHECK_IGNORE_JSRULES;

	private static ProjectConfiguration instance;

	/**
	 * This property can be used to overwrite the local project folder. This should only be used for tests.
	 */
	public static final String RETEST_PROJECT_ROOT = "de.retest.recheck.project.root";

	private ProjectConfiguration() {

	}

	public static ProjectConfiguration getInstance() {
		if ( instance == null ) {
			instance = new ProjectConfiguration();
		}
		return instance;
	}

	public void ensureProjectConfigurationInitialized() {
		final Path projectRoot = ProjectRootFinderUtil.getProjectRoot()
				.orElseThrow( () -> new RuntimeException( "Project root could not be found." ) );
		final Path projectConfigFolder = projectRoot.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		final Path projectFilterFolder = projectConfigFolder.resolve( FILTER_FOLDER );
		final Path projectConfigFile = projectConfigFolder.resolve( RETEST_PROJECT_PROPERTIES );
		final Path projectIgnoreFile = projectConfigFolder.resolve( RECHECK_IGNORE );
		final Path projectRuleIgnoreFile = projectConfigFolder.resolve( RECHECK_IGNORE_JSRULES );

		createProjectConfigurationFolderIfNeeded( projectConfigFolder );
		createEmptyProjectConfigurationIfNeeded( projectConfigFile, RETEST_PROJECT_DEFAULTS );
		createEmptyProjectConfigurationIfNeeded( projectIgnoreFile, RECHECK_IGNORE_DEFAULTS );
		createEmptyProjectConfigurationIfNeeded( projectRuleIgnoreFile, RECHECK_IGNORE_JSRULES_DEFAULTS );
		createEmptyFolderIfNeeded( projectFilterFolder );
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
			try ( final InputStream is = getInputStreamFrom( defaultFile ) ) {
				Files.copy( is, configFile );
				logger.info( "Creating empty recheck configuration in {}.", configFile );
			} catch ( final IOException e ) {
				logger.error( "Error creating empty recheck configuration in {}.", configFile );
			}
		}
	}

	private void createEmptyFolderIfNeeded( final Path configFolder ) {
		if ( !configFolder.toFile().exists() ) {
			try {
				Files.createDirectories( configFolder );
			} catch ( final IOException e ) {
				logger.error( "Error creating empty recheck configuration in {}.", configFolder );
			}
		}
	}

	private InputStream getInputStreamFrom( final String fileName ) {
		return getClass().getClassLoader().getResourceAsStream( fileName );
	}

}
