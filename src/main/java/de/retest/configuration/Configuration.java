package de.retest.configuration;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.util.Properties;

import de.retest.util.FileUtil;

public class Configuration {

	/**
	 * Package private for tests only! Use {@link RetestWorkspace#getPropertiesFileArgument()} instead.
	 */
	public static final String PROP_CONFIG_FILE_PATH = "de.retest.configFile";

	public static final String RETEST_PROPERTIES_FILE_NAME = "retest.properties";

	private static Configuration instance;

	private static final Object lockField = new Object[0];

	static boolean isLoaded() {
		return instance != null && instance.retestWorkspace != null;
	}

	public static void ensureLoaded() {
		synchronized ( lockField ) {
			if ( instance == null ) {
				instance = new Configuration();
			}
		}
		instance.systemPropertyHandler.checkSystemPropertyStillIsCorrect();
	}

	private final SystemPropertyHandler systemPropertyHandler;
	private final RetestWorkspace retestWorkspace;

	private Configuration() {
		systemPropertyHandler = new SystemPropertyHandler();
		final File configFile = RetestWorkspace.tryToFindConfigFile();
		if ( configFile != null ) {
			systemPropertyHandler.loadUserPropertiesFile( configFile );
		}
		retestWorkspace = new RetestWorkspace( configFile );
	}

	public static synchronized void setConfigFile( final File configFile ) throws ConfigurationException {
		ensureLoaded();
		final File verifiedFile = FileUtil.readableCanonicalFileOrNull( configFile );
		if ( verifiedFile != null ) {
			instance.systemPropertyHandler.loadUserPropertiesFile( verifiedFile );
			instance = new Configuration( instance.systemPropertyHandler, new RetestWorkspace( verifiedFile ) );
		} else {
			throw new ConfigurationException( new Property( Configuration.PROP_CONFIG_FILE_PATH ),
					"Configuration file '" + canonicalPathQuietly( configFile )
							+ "' doesn't exists or isn't readable!" );
		}
	}

	private Configuration( final SystemPropertyHandler systemPropertyHandler, final RetestWorkspace reTestWorkspace ) {
		this.systemPropertyHandler = systemPropertyHandler;
		retestWorkspace = reTestWorkspace;
	}

	/**
	 * Only for tests!!!
	 */
	public static synchronized void resetRetest() {
		if ( instance != null ) {
			instance.systemPropertyHandler.tearDown();
			instance = null;
		}
	}

	// getter

	public static File getRetestWorkspace() {
		ensureLoaded();
		if ( !instance.retestWorkspace.workspaceFolder.exists() ) {
			instance.retestWorkspace.workspaceFolder.mkdirs();
		}
		return instance.retestWorkspace.workspaceFolder;
	}

	public static String getPropertiesFileArgument() {
		ensureLoaded();
		return instance.retestWorkspace.getPropertiesFileArgument();
	}

	public static File getUserPropertiesFile() {
		ensureLoaded();
		return instance.retestWorkspace.getPropertiesFile();
	}

	public static Properties getUserConfigProps() {
		final Properties result = new Properties();
		// userConfigFileProps overwrites retestDefaultProps
		result.putAll( instance.systemPropertyHandler.getUserConfigFileProps() );
		// originConsoleProps overwrites userConfigFileProps,
		result.putAll( instance.systemPropertyHandler.getOriginConsoleProps() );
		return result;
	}
}
