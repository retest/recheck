package de.retest.recheck;

public class Properties {

	public static final String STATE_OUTPUT_FORMAT_PROPERTY = "de.retest.output.Format";
	public static final String REPORT_OUTPUT_FORMAT_PROPERTY = "de.retest.recheck.output.reportFormat";
	public static final String CONFIG_FILE_PROPERTY = "de.retest.configFile";

	public static final String PROPERTY_VALUE_SEPARATOR = ";";
	public static final String ZIP_FOLDER_SEPARATOR = "/";

	public static final double WINDOW_MATCH_THRESHOLD = 0.8;
	public static final double WINDOW_CHILDS_MATCH_THRESHOLD = 0.5;

	public static final double ELEMENT_MATCH_THRESHOLD_DEFAULT = 0.3;
	public static final String ELEMENT_MATCH_THRESHOLD_PROPERTY = "de.retest.recheck.elementMatchThreshold";

	public static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	public static final String RECHECK_FOLDER_NAME = "recheck";
	public static final String DEFAULT_XML_FILE_NAME = "retest.xml";
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
		final String format = System.getProperty( REPORT_OUTPUT_FORMAT_PROPERTY, FileOutputFormat.KRYO.toString() );
		return FileOutputFormat.valueOf( format );
	}

	public static FileOutputFormat getStateOutputFormat() {
		final String format = System.getProperty( STATE_OUTPUT_FORMAT_PROPERTY, FileOutputFormat.PLAIN.toString() );
		if ( format.equalsIgnoreCase( FileOutputFormat.ZIP.toString() ) ) {
			return FileOutputFormat.ZIP;
		}
		return FileOutputFormat.PLAIN;
	}
}
