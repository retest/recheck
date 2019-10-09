package de.retest.recheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Properties {

	private static final Logger logger = LoggerFactory.getLogger( Properties.class );

	public static final String FILE_OUTPUT_FORMAT_PROPERTY = "de.retest.output.Format";
	public static final String CONFIG_FILE_PROPERTY = "de.retest.configFile";

	public static final String REHUB_REPORT_UPLOAD_ENABLED = "de.retest.recheck.rehub.reportUploadEnabled";

	public static final String PROPERTY_VALUE_SEPARATOR = ";";
	public static final String ZIP_FOLDER_SEPARATOR = "/";

	public static final double WINDOW_MATCH_THRESHOLD = 0.8;
	public static final double WINDOW_CHILDS_MATCH_THRESHOLD = 0.5;

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

	public static enum FileOutputFormat {
		PLAIN,
		ZIP,
		KRYO,
		CLOUD
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

	public static Double getConfiguredDouble( final String property, final Double defaultValue ) {
		final String value = System.getProperty( property );
		if ( value == null ) {
			return defaultValue;
		}
		try {
			return Double.parseDouble( value );
		} catch ( final NumberFormatException e ) {
			logger.error( "Exception parsing value {} of property {} to double, using default {} instead.", value,
					property, defaultValue );
			return defaultValue;
		}
	}
}
