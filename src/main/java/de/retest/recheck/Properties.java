package de.retest.recheck;

import static de.retest.recheck.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.ArrayUtils;

import de.retest.recheck.configuration.ConfigurationException;
import de.retest.recheck.configuration.Property;
import de.retest.recheck.util.VersionProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Properties {

	private Properties() {}

	private static final String JVM_EXECUTION_DIR = "user.dir";
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( Properties.class );

	public static final String FILE_OUTPUT_FORMAT_PROPERTY = "de.retest.output.Format";
	public static final String CONFIG_FILE_PROPERTY = "de.retest.configFile";

	public static final String REHUB_REPORT_UPLOAD_ENABLED = "de.retest.recheck.rehub.reportUploadEnabled";

	public static final String PROPERTY_VALUE_SEPARATOR = ";";
	public static final String ZIP_FOLDER_SEPARATOR = "/";

	public static final String INSTALL_DIRECTORY = "de.retest.Dir";
	public static final String WORK_DIRECTORY = "de.retest.workDirectory";

	public static final double ROOT_ELEMENT_MATCH_THRESHOLD_DEFAULT = 0.8;
	public static final String ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY = "de.retest.recheck.rootElementMatchThreshold";
	public static final double ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_DEFAULT = 0.5;
	public static final String ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY =
			"de.retest.recheck.rootElementContainedChildrenMatchThreshold";

	public static final double ELEMENT_MATCH_THRESHOLD_DEFAULT = 0.3;
	public static final String ELEMENT_MATCH_THRESHOLD_PROPERTY = "de.retest.recheck.elementMatchThreshold";

	public static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	public static final String RECHECK_FOLDER_NAME = "recheck";
	public static final String DEFAULT_XML_FILE_NAME = "retest.xml";

	public static final String RETEST_FOLDER_NAME = ".retest";
	public static final String RETEST_PROPERTIES_FILE_NAME = "retest.properties";

	public static final String GOLDEN_MASTER_FILE_EXTENSION = ".recheck";
	public static final String TEST_REPORT_FILE_EXTENSION = ".report";
	public static final String AGGREGATED_TEST_REPORT_FILE_NAME = "tests" + TEST_REPORT_FILE_EXTENSION;

	public enum FileOutputFormat {
		PLAIN,
		ZIP,
		KRYO,
		CLOUD
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
		return null;
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

	private static final String[] INSTALL_DIR_FILE_LIST = { "review.jar", "review", "review.exe", "review.bat" };

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

	public static FileOutputFormat getReportOutputFormat() {
		final String format = System.getProperty( FILE_OUTPUT_FORMAT_PROPERTY, FileOutputFormat.KRYO.toString() );
		if ( Boolean.getBoolean( REHUB_REPORT_UPLOAD_ENABLED ) ) {
			return FileOutputFormat.CLOUD;
		}
		return FileOutputFormat.valueOf( format );
	}

	public static FileOutputFormat getStateOutputFormat() {
		final String format = System.getProperty( FILE_OUTPUT_FORMAT_PROPERTY, FileOutputFormat.PLAIN.toString() );
		if ( format.equalsIgnoreCase( FileOutputFormat.ZIP.toString() ) ) {
			return FileOutputFormat.ZIP;
		}
		return FileOutputFormat.PLAIN;
	}

	public static double getElementMatchThreshhold() {
		return getConfiguredDouble( ELEMENT_MATCH_THRESHOLD_PROPERTY, ELEMENT_MATCH_THRESHOLD_DEFAULT );
	}

	public static double getMinimumWindowMatchThreshold() {
		return getConfiguredDouble( ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY, ROOT_ELEMENT_MATCH_THRESHOLD_DEFAULT );
	}

	public static double getMinimumContainedComponentsMatchThreshold() {
		return getConfiguredDouble( ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY,
				ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_DEFAULT );
	}

	public static double getConfiguredDouble( final String property, final double defaultValue ) {
		final String value = System.getProperty( property );
		if ( value == null ) {
			return defaultValue;
		}
		try {
			return Double.parseDouble( value );
		} catch ( final NumberFormatException e ) {
			log.error( "Exception parsing value {} of property {} to double, using default {} instead.", value,
					property, defaultValue );
			return defaultValue;
		}
	}
}
