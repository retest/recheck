package de.retest.configuration;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import de.retest.util.FileUtil;

class SystemPropertyHandler {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( SystemPropertyHandler.class );

	private final Properties originConsoleProps;
	private final Properties retestDefaultProps;
	private final Properties userConfigFileProps;
	private final Properties runtimeProps;

	public Properties newSystemProps;

	SystemPropertyHandler() {
		originConsoleProps = System.getProperties();

		retestDefaultProps = loadDefaultPropertiesFile();

		userConfigFileProps = new Properties();

		// order is relevant:
		// runtimeProps overwrites originConsoleProps,
		// originConsoleProps overwrites userConfigFileProps,
		// userConfigFileProps overwrites retestDefaultProps
		runtimeProps = new Properties(
				new PropertiesList( originConsoleProps, userConfigFileProps, retestDefaultProps ) );
		newSystemProps = new PropertyVariableResolver( new SpecialProperties( runtimeProps ) );
		// replaces the originConsoleProps
		System.setProperties( newSystemProps );
	}

	/**
	 * Some applications replace the system properties, be aware of this!
	 */
	void checkSystemPropertyStillIsCorrect() {
		if ( newSystemProps != System.getProperties() ) {
			synchronized ( this ) {
				if ( newSystemProps != System.getProperties() ) {
					System.setProperties( newSystemProps );
					logger.error( "Someone changed the system properties!",
							new IllegalStateException( "Someone changed the system properties!" ) );
				}
			}
		}
	}

	/**
	 * This method is used after init of ReTestWorkspace to load userConfigFile.
	 */
	void loadUserPropertiesFile( final File userConfigFile ) {
		logger.info( "Loading configuration from '{}'.", userConfigFile );
		final String fileContent = FileUtil.readFileToString( userConfigFile );
		try {
			// so we can read windows paths directly..
			userConfigFileProps.load( new StringReader( fileContent.replace( "\\", "\\\\" ) ) );
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}

	private Properties loadDefaultPropertiesFile() {
		try {
			final Properties props = new Properties();
			props.load( Configuration.class.getResourceAsStream( "/retest-defaults.properties" ) );
			logger.debug( "Loaded default properties file with values: {}.", props );
			return props;
		} catch ( final Exception e ) {
			throw new RuntimeException( "Exception while loading default properties file!", e );
		}
	}

	public void tearDown() {
		System.setProperties( originConsoleProps );
		newSystemProps.clear();
	}

	public Properties getOriginConsoleProps() {
		return originConsoleProps;
	}

	public Properties getRetestDefaultProps() {
		return retestDefaultProps;
	}

	public Properties getUserConfigFileProps() {
		return userConfigFileProps;
	}

	public Properties getRuntimeProps() {
		return runtimeProps;
	}
}
