package de.retest;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.retest.configuration.ConfigurationException;
import de.retest.configuration.Property;
import de.retest.util.VersionProvider;

public class Properties {

	private static final String JVM_EXECUTION_DIR = "user.dir";
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( Properties.class );

	public static final String VALUES_SEPARATOR = ";";

	public static final String TEST_CONTEXT_CLASS = "de.retest.TestContextClass";

	public static final String APPLICATION_PATH = "de.retest.sut.applicationPath";
	public static final String APPLICATION_CLASSPATH_ELEMENTS = "de.retest.sut.applicationClassPath";

	/**
	 * Use {@link Properties#getReTestInstallDir()}
	 */
	public static final String INSTALL_DIRECTORY = "de.retest.Dir";
	public static final String WORK_DIRECTORY = "de.retest.workDirectory";

	public static final String PARENT_PACKAGE = "de.retest.sut.parentPackage";
	public static final String MAIN_CLASS = "de.retest.sut.mainClass";

	public static final String TESTRESULTS_REPORTSDIR = "de.retest.testresults.reportsDir";

	public static final String TESTREPORT_XSL = "de.retest.testresults.testreport.xsl";
	public static final String TESTREPORT_HTMLASSETS = "de.retest.testresults.testreport.htmlAssets";

	public static final String DEVELOPER_MODE = "de.retest.Development";
	public static final String FILE_OUTPUT_FORMAT = "de.retest.output.Format";

	public static final String REMOTE_DEBUG = "de.retest.remote.debug";
	public static final String REMOTE_CALLS_ENABLED = "de.retest.remote.enabled";

	public static enum FileOutputFormat {
		PLAIN,
		ZIP,
		KRYO
	}

	public static final String ZIP_FOLDER_SEPARATOR = "/";
	public static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	public static final String DEFAULT_XML_FILE_NAME = "retest.xml";

	public static final String TESTRESULTS_REPORTSDIR_DEFAULT = "testreports";

	public static final String PROXY_ADRESS = "de.retest.internet.proxy.adress";
	public static final String PROXY_PORT = "de.retest.internet.proxy.port";
	public static final String PROXY_USERNAME = "de.retest.internet.proxy.username";
	public static final String PROXY_PASSWORD = "de.retest.internet.proxy.password";

	public static final String USE_DEFAULT_AUTHENTICATOR = "de.retest.internet.useDefaultAuthenticator";

	public static final String FEEDBACK_DISABLED = "de.retest.feedback.disabled";

	/**
	 * Extra threshold for windows, because we have only a few informations for those.
	 */
	public static final double WINDOW_MATCH_THRESHOLD = 0.8;

	/**
	 * If no window is found with WINDOW_MATCH_THRESHOLD, all windows are compared by content with this threshold.
	 */
	public static final double WINDOWCHILDS_MATCH_THRESHOLD = 0.5;

	public static final String REPORT_GENERATION_IN_SEPARATE_JVM = "de.retest.reportGenerationInSeparateJvm";

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

	public static List<String> getPath() {
		return Arrays.asList( System.getenv( "PATH" ).split( File.pathSeparator ) );
	}

}
