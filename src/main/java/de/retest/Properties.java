package de.retest;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.retest.configuration.Configuration;
import de.retest.configuration.ConfigurationException;
import de.retest.configuration.Property;
import de.retest.util.FileUtil;
import de.retest.util.VersionProvider;

public class Properties {

	private static final String JVM_EXECUTION_DIR = "user.dir";
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( Properties.class );

	public static final String VALUES_SEPARATOR = ";";

	public static final String TEST_CONTEXT_CLASS = "de.retest.TestContextClass";
	public static final String CRASH_DETECTORS = "de.retest.CrashDetectors";
	public static final String MODULE_FILTER = "de.retest.filter.module";
	public static final String APPLICATION_PATH = "de.retest.sut.applicationPath";
	public static final String APPLICATION_CLASSPATH_ELEMENTS = "de.retest.sut.applicationClassPath";
	public static final String INSTRUMENT_APPLICATION = "de.retest.instrumentation.shouldInstrument";
	/**
	 * Use {@link Properties#getReTestInstallDir()}
	 */
	public static final String INSTALL_DIRECTORY = "de.retest.Dir";
	public static final String WORK_DIRECTORY = "de.retest.workDirectory";
	public static final String SUT_JAVA_EXECUTABLE = "de.retest.sut.java";
	public static final String JAVA_AGENT_PATH_PROPERTY = "de.retest.sut.javaagent";
	public static final String PARENT_PACKAGE = "de.retest.sut.parentPackage";
	public static final String MAIN_CLASS = "de.retest.sut.mainClass";
	public static final String MAIN_ARGS = "de.retest.sut.mainArgs";
	public static final String VM_DEFAULT_ARGS = "de.retest.vmArgs";
	public static final String VM_ADDITIONAL_ARGS = "de.retest.sut.additionalVmArgs";

	/**
	 * Absolute path to the bootclasspath/patch-module path to use and e.g. generate class files to.
	 */
	public static final String BOOTCLASSPATH = "de.retest.bootclasspath";

	public static final String LOGBACK_CONFIG = "logback.configurationFile";
	public static final String TESTRESULTS_REPORTSDIR = "de.retest.testresults.reportsDir";
	public static final String TESTREPORT = "de.retest.testresults.testreport";
	public static final String TESTREPORT_XSL = "de.retest.testresults.testreport.xsl";
	public static final String TESTREPORT_HTMLASSETS = "de.retest.testresults.testreport.htmlAssets";
	public static final String REPLAY_RECORDED = "de.retest.replayRecorded";
	public static final String DEVELOPER_MODE = "de.retest.Development";
	public static final String FILE_OUTPUT_FORMAT = "de.retest.output.Format";
	public static final String EXECUTABLE = "de.retest.executable";
	public static final String EXECUTION_TRACER = "de.retest.ExecutionTracer";
	public static final String REMOTE_DEBUG = "de.retest.remote.debug";
	public static final String REMOTE_CALLS_ENABLED = "de.retest.remote.enabled";
	public static final String REMOTE_IS_CLIENT = "de.retest.remote.isClient";
	public static final String REMOTE_WITHOUT_GUI = "de.retest.remote.withoutGui";
	public static final String REPLACE_CALLS_TO_EXIT = "de.retest.javaagent.replaceCallsToExit";

	public static final String TARGET_NOT_FOUND_WAITING_TIME = "de.retest.targetNotFound.waitingTime";
	public static final long TARGET_NOT_FOUND_WAITING_TIME_DEFAULT = 30000L;
	public static final String TARGET_NOT_FOUND_SLEEP_TIME = "de.retest.targetNotFound.sleepTime";
	public static final long TARGET_NOT_FOUND_SLEEP_TIME_DEFAULT = 500L;

	public static final String TESTCOVERAGE_TARGET_VALUE = "de.retest.coverage.TargetValue";

	public static enum FileOutputFormat {
		PLAIN,
		ZIP,
		KRYO
	}

	public static final String ZIP_FOLDER_SEPARATOR = "/";
	public static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	public static final String DEFAULT_XML_FILE_NAME = "retest.xml";

	// codeCoverageTimeIntervalOption = new Option<Integer>(-1);
	public static final String CODE_COVERAGE_TIME_INTERVAL = "de.retest.codeCoverage.TimeInterval";
	// populationSizeOption = new Option<Integer>(3);
	public static final String POPULATION_SIZE = "de.retest.ga.PopulationSize";
	// testGeneratorTimeLimitOption = new Option<Integer>(600);
	public static final String TEST_GENERATOR_TIME_LIMIT_IN_MINUTES = "de.retest.testGenerator.TimeLimit";
	public static final String TEST_GENERATOR_ACTIONS_LIMIT = "de.retest.testGenerator.ActionsLimit";
	public static final String TEST_GENERATOR_GENERATIONS_LIMIT = "de.retest.testGenerator.GenerationsLimit";
	public static final String ADAPT_SUITE_LENGTH = "de.retest.testGenerator.adaptSuiteLength";
	private static final String SHOULD_CHECK_JAR_SIGNATURE = "de.retest.checkAndRemoveJarSignature";

	public static final String TESTRESULTS_REPORTSDIR_DEFAULT = "testreports";

	public static final String LISTENER_BLACKLIST = "de.retest.listener.blacklist";
	public static final String INSTRUMENTATION_BLACKLIST = "de.retest.instrumentation.blacklist";

	public static final String PROXY_ADRESS = "de.retest.internet.proxy.adress";
	public static final String PROXY_PORT = "de.retest.internet.proxy.port";
	public static final String PROXY_USERNAME = "de.retest.internet.proxy.username";
	public static final String PROXY_PASSWORD = "de.retest.internet.proxy.password";

	public static final String USE_DEFAULT_AUTHENTICATOR = "de.retest.internet.useDefaultAuthenticator";

	public static final String FEEDBACK_DISABLED = "de.retest.feedback.disabled";

	public static final String GENERIC_EXECUTABLE = "de.retest.launcher.genericExecutable";
	public static final String GENERIC_PARAMS = "de.retest.launcher.genericParams";

	/**
	 * Extra threshold for windows, because we have only a few informations for those.
	 */
	public static final double WINDOW_MATCH_THRESHOLD = 0.8;

	/**
	 * If no window is found with WINDOW_MATCH_THRESHOLD, all windows are compared by content with this threshold.
	 */
	public static final double WINDOWCHILDS_MATCH_THRESHOLD = 0.5;

	public static final String REPORT_GENERATION_IN_SEPARATE_JVM = "de.retest.reportGenerationInSeparateJvm";

	public static final String MAX_CHILD_COMPONENTS_THRESHOLD_PROPERTY = "de.retest.maxChildComponents";
	private static final int MAX_CHILD_COMPONENTS_THRESHOLD_DEFAULT = 1500;

	/**
	 * Maximum number of child components for JList, JTable, and JTree.
	 *
	 * @return amount of maximum number of child components
	 */
	public static final int getMaxChildComponentsThreshold() {
		return Integer.getInteger( MAX_CHILD_COMPONENTS_THRESHOLD_PROPERTY, MAX_CHILD_COMPONENTS_THRESHOLD_DEFAULT );
	}

	public static String getNotNullProperty( final String property ) throws NullPointerException {
		final String result = System.getProperty( property );
		if ( result == null ) {
			logger.error( "Property '{}' was not set!", property );
			throw new ConfigurationException( new Property( property ), "Property '" + property + "' was not set!" );
		}
		return result;
	}

	public static File getReTestInstallDir() {
		final String configuredInstallDir = System.getProperty( INSTALL_DIRECTORY );
		if ( configuredInstallDir != null ) {
			final File file = new File( configuredInstallDir );
			if ( containsReTestInstallation( file ) ) {
				return file;
			} else {
				logger.error( "Property {}={} points to directory without retest installation!", INSTALL_DIRECTORY,
						canonicalPathQuietly( file ) );
			}
		}

		final File jarFolder = getJarFolderForThisClass();
		if ( jarFolder != null ) {
			if ( containsReTestInstallation( jarFolder.getParentFile() ) ) {
				// default case for files in lib folder
				return jarFolder.getParentFile();
			} else if ( containsReTestInstallation( jarFolder ) ) {
				// case for fat jars in install dir
				return jarFolder;
			}
		}

		// use project folder as install dir in eclipse
		if ( isDevEnvironment() ) {
			return new File( System.getProperty( JVM_EXECUTION_DIR ) );
		}

		throw new ConfigurationException( new Property( INSTALL_DIRECTORY ),
				"Configured retest install directory is wrong!" );
	}

	public static File getSutJavaAgent() {
		final String path = System.getProperty( JAVA_AGENT_PATH_PROPERTY, "retest-javaagent.jar" );
		final File file = new File( getReTestInstallDir(), path );
		if ( !file.exists() ) {
			final String message = "JavaAgent path relative to install directory not found '"
					+ FileUtil.canonicalPathQuietly( file ) + "'";
			throw new RuntimeException( new FileNotFoundException( message ) );
		}
		return file;
	}

	public static String getSutJavaExecutable() {
		return System.getProperty( SUT_JAVA_EXECUTABLE, "java" );
	}

	public static boolean isDevEnvironment() {
		if ( VersionProvider.class.desiredAssertionStatus() ) {
			return true;
		}
		final String location = Properties.class.getProtectionDomain().getCodeSource().getLocation().toString();
		if ( !location.endsWith( ".jar" ) ) {
			if ( !VersionProvider.class.desiredAssertionStatus() ) {
				throw new ConfigurationException( new Property( "-ea", "not set" ),
						"This seems to be a development environment, but assertions are not enabled!."
								+ " Enable assertions using -ea!" );
			}
			return true;
		}
		return false;
	}

	private static File getJarFolderForThisClass() {
		final URL jarLoc = Properties.class.getProtectionDomain().getCodeSource().getLocation();
		if ( jarLoc.toExternalForm().endsWith( ".jar" ) ) {
			try {
				return new File( jarLoc.toURI() ).getParentFile();
			} catch ( final URISyntaxException e ) {
				// ignore
			}
		}
		return null;
	}

	private static final String[] INSTALL_DIR_FILE_LIST =
			{ "retest-all.jar", "retest", "retest-gui.exe", "retest.bat" };

	private static boolean containsReTestInstallation( final File installDirCandidate ) {
		int c = 0;

		if ( installDirCandidate.getName().equals( "retest" ) ) {
			c++;
		}

		final String[] files = installDirCandidate.list();
		if ( files != null ) {
			for ( final String file : files ) {
				if ( ArrayUtils.contains( INSTALL_DIR_FILE_LIST, file ) ) {
					c++;
				}
			}
		}

		return c > 2;
	}

	public static File getSutExecutionDir() {
		final String appPath = System.getProperty( Properties.APPLICATION_PATH );
		if ( appPath != null && !appPath.isEmpty() ) {
			logger.info( "SUT execution directory set by '{}' property ('{}').", Properties.APPLICATION_PATH, appPath );
			final File sutExecDir = new File( appPath );
			if ( sutExecDir.isFile() ) {
				return sutExecDir.getParentFile();
			} else {
				if ( !sutExecDir.exists() && !sutExecDir.mkdirs() ) {
					throw new ConfigurationException( new Property( Properties.APPLICATION_PATH ),
							"Configured application path '" + FileUtil.canonicalPathQuietly( sutExecDir )
									+ "' is not valid!" );
				}
				return sutExecDir;
			}
		}

		final File defaultSutDir = getDefaultSutDir();
		logger.info( "SUT execution directory set by default ('{}').", defaultSutDir );
		if ( !defaultSutDir.exists() || !defaultSutDir.isDirectory() ) {
			throw new ConfigurationException( new Property( Properties.APPLICATION_PATH ),
					"Default SUT dir '" + FileUtil.canonicalPathQuietly( defaultSutDir ) + "' is not valid!" );
		}
		return defaultSutDir;
	}

	public static File getDefaultSutDir() {
		return new File( getReTestInstallDir().getParentFile(), "system-under-test" );
	}

	public static FileOutputFormat getFileOutputFormat() {
		final String format = System.getProperty( FILE_OUTPUT_FORMAT, FileOutputFormat.PLAIN.toString() );
		if ( format.equalsIgnoreCase( FileOutputFormat.ZIP.toString() ) ) {
			return FileOutputFormat.ZIP;
		}
		return FileOutputFormat.PLAIN;
	}

	public static boolean getBooleanWithTrueAsDefault( final String key ) {
		final String value = System.getProperty( key );
		if ( value == null ) {
			return true;
		}
		return value.equalsIgnoreCase( "false" ) ? false : true;
	}

	public static boolean getBooleanWithFalseAsDefault( final String key ) {
		return Boolean.getBoolean( key );
	}

	public static boolean shouldInstrument() {
		return getBooleanWithTrueAsDefault( INSTRUMENT_APPLICATION );
	}

	public static boolean shouldCheckAndRemoveJarSignature() {
		return getBooleanWithFalseAsDefault( SHOULD_CHECK_JAR_SIGNATURE );
	}

	public static Set<String> getListenerBlacklist() {
		final String blacklist = System.getProperty( LISTENER_BLACKLIST );
		if ( StringUtils.isEmpty( blacklist ) ) {
			return Collections.emptySet();
		}
		return new HashSet<>( Arrays.asList( blacklist.split( VALUES_SEPARATOR ) ) );
	}

	public static Set<String> getInstrumentationBlacklist() {
		final String blacklist = System.getProperty( INSTRUMENTATION_BLACKLIST );
		if ( StringUtils.isEmpty( blacklist ) ) {
			return Collections.emptySet();
		}
		return new HashSet<>( Arrays.asList( blacklist.split( VALUES_SEPARATOR ) ) );
	}

	/**
	 * When the model contains less than this many values, we will give each value a physically separate action, that
	 * can result in a different target state...
	 *
	 * @return the threshold
	 **/
	public static int getDistinguishBetweenValueThreshold() {
		// TODO Make this configurable
		return 12;
	}

	public static final String LAUNCHER = "de.retest.launcher";
	public static final String DEMO_LAUNCHER_NAME = "de.retest.launcher.DemoLauncher";

	public static boolean remoteCallsEnabled() {
		if ( isDemo() ) {
			return false;
		}
		return Boolean.getBoolean( REMOTE_CALLS_ENABLED );
	}

	public static boolean isDemo() {
		final String launcherName = System.getProperty( Properties.LAUNCHER );
		return StringUtils.equals( launcherName, DEMO_LAUNCHER_NAME );
	}

	public static boolean isRemoteClient() {
		return Boolean.getBoolean( REMOTE_IS_CLIENT );
	}

	public static boolean isRemoteWithoutGui() {
		return Boolean.getBoolean( REMOTE_WITHOUT_GUI );
	}

	public static String getExecutable() {
		return getReTestInstallDir() + File.separator + "retest-all.jar";
	}

	public static List<String> getPath() {
		return Arrays.asList( System.getenv( "PATH" ).split( File.pathSeparator ) );
	}

	public static final File getScriptsFolder() {
		return new File( Configuration.getRetestWorkspace(), "scripts" );
	}

	public static boolean shouldReplaceCallsToExit() {
		return Boolean.getBoolean( REPLACE_CALLS_TO_EXIT );
	}

}
