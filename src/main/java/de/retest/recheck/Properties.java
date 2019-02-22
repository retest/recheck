package de.retest.recheck;

public class Properties {
	public static final String VALUES_SEPARATOR = ";";
	public static final String FILE_OUTPUT_FORMAT = "de.retest.output.Format";
	public static final String DEFAULT_XML_FILE_NAME = "retest.xml";
	public static final String ZIP_FOLDER_SEPARATOR = "/";
	public static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	public static final double WINDOW_MATCH_THRESHOLD = 0.8;
	public static final double WINDOWCHILDS_MATCH_THRESHOLD = 0.5;
	public static final String RECHECK_FILE_EXTENSION = ".recheck";
	public static final String RETEST_PROPERTIES_FILE_NAME = "retest.properties";
	public static final String PROP_CONFIG_FILE_PATH = "de.retest.configFile";
	public static final String RECHECK_FOLDER_NAME = "recheck";
	public static final String REPORT_FILE_EXTENSION = ".result";

	public static enum FileOutputFormat {
		PLAIN,
		ZIP,
		KRYO
	}

	public static FileOutputFormat getFileOutputFormat() {
		final String format = System.getProperty( FILE_OUTPUT_FORMAT, FileOutputFormat.PLAIN.toString() );
		if ( format.equalsIgnoreCase( FileOutputFormat.ZIP.toString() ) ) {
			return FileOutputFormat.ZIP;
		}
		return FileOutputFormat.PLAIN;
	}
}
