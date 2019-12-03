package de.retest.recheck.configuration;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.RecheckProperties;
import de.retest.recheck.ignore.SearchFilterFiles;

public class ProjectConfiguration {

	private static final Logger logger = LoggerFactory.getLogger( ProjectConfiguration.class );

	public static final String RETEST_PROJECT_PROPERTIES = "retest.properties";
	public static final String RECHECK_IGNORE = "recheck.ignore";
	public static final String RECHECK_IGNORE_JSRULES = "recheck.ignore.js";

	private static final String DEFAULT_PREFIX = "default-";
	private static final String RETEST_PROJECT_DEFAULTS = DEFAULT_PREFIX + RETEST_PROJECT_PROPERTIES;
	private static final String RECHECK_IGNORE_DEFAULTS = DEFAULT_PREFIX + RECHECK_IGNORE;
	private static final String RECHECK_IGNORE_JSRULES_DEFAULTS = DEFAULT_PREFIX + RECHECK_IGNORE_JSRULES;

	private static ProjectConfiguration instance;

	/**
	 * This property can be used to overwrite the local project folder.
	 */
	public static final String RETEST_PROJECT_ROOT = "de.retest.recheck.project.root";

	private ProjectConfiguration() {}

	public static ProjectConfiguration getInstance() {
		if ( instance == null ) {
			instance = new ProjectConfiguration();
		}
		return instance;
	}

	public Optional<Path> getProjectConfigFolder() {
		return ProjectRootFinderUtil.getProjectRoot().map( path -> path.resolve( RETEST_FOLDER_NAME ) );
	}

	public Path findProjectConfigFolder() {
		final String msg = String.format(
				"Project root could not be found. Please set the property '%s' to point to the project root (containing e.g. the %s folder).",
				RETEST_PROJECT_ROOT, RETEST_FOLDER_NAME );
		return getProjectConfigFolder().orElseThrow( () -> new RuntimeException( msg ) );
	}

	public void ensureProjectConfigurationInitialized() {
		final Path projectFilterFolder = findProjectConfigFolder().resolve( SearchFilterFiles.FILTER_DIR_NAME );
		final Path projectConfigFile = findProjectConfigFolder().resolve( RETEST_PROJECT_PROPERTIES );
		final Path projectIgnoreFile = findProjectConfigFolder().resolve( RECHECK_IGNORE );
		final Path projectRuleIgnoreFile = findProjectConfigFolder().resolve( RECHECK_IGNORE_JSRULES );

		createProjectConfigurationFolderIfNeeded( findProjectConfigFolder() );
		createEmptyProjectConfigurationIfNeeded( projectConfigFile, RETEST_PROJECT_DEFAULTS );
		createEmptyProjectConfigurationIfNeeded( projectIgnoreFile, RECHECK_IGNORE_DEFAULTS );
		createEmptyProjectConfigurationIfNeeded( projectRuleIgnoreFile, RECHECK_IGNORE_JSRULES_DEFAULTS );
		createEmptyFolderIfNeeded( projectFilterFolder );

		RecheckProperties.init();
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
				logger.error( "Error creating empty filter folder in {}.", configFolder );
			}
		}
	}

	private InputStream getInputStreamFrom( final String fileName ) {
		return getClass().getClassLoader().getResourceAsStream( fileName );
	}

}
