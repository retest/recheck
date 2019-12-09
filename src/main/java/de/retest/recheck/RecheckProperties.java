package de.retest.recheck;

import java.util.List;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigCache;
import org.aeonbits.owner.ConfigFactory;

import de.retest.recheck.configuration.ProjectRootFinderUtil;
import de.retest.recheck.persistence.FileOutputFormat;

@Sources( "file:${projectroot}/.retest/retest.properties" )
public interface RecheckProperties extends Config {

	/*
	 * Basic usage.
	 */

	static void init() {
		ProjectRootFinderUtil.getProjectRoot().ifPresent(
				projectRoot -> ConfigFactory.setProperty( "projectroot", projectRoot.toAbsolutePath().toString() ) );
	}

	static RecheckProperties getInstance() {
		return ConfigCache.getOrCreate( RecheckProperties.class, System.getProperties() );
	}

	/*
	 * Various constants.
	 */

	static final String PROPERTY_VALUE_SEPARATOR = ";";
	static final String ZIP_FOLDER_SEPARATOR = "/";

	static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	static final String RECHECK_FOLDER_NAME = "recheck";
	static final String DEFAULT_XML_FILE_NAME = "retest.xml";

	static final String RETEST_FOLDER_NAME = ".retest";
	static final String RETEST_PROPERTIES_FILE_NAME = "retest.properties";

	static final String GOLDEN_MASTER_FILE_EXTENSION = ".recheck";
	static final String TEST_REPORT_FILE_EXTENSION = ".report";
	static final String AGGREGATED_TEST_REPORT_FILE_NAME = "tests" + TEST_REPORT_FILE_EXTENSION;

	/*
	 * Properties, their key constants and related functionality.
	 */

	static final String IGNORE_ATTRIBUTES_PROPERTY_KEY = "de.retest.recheck.ignore.attributes";

	@Key( IGNORE_ATTRIBUTES_PROPERTY_KEY )
	@DefaultValue( "" )
	@Separator( PROPERTY_VALUE_SEPARATOR )
	List<String> ignoreAttributes();

	static final String ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY = "de.retest.recheck.elementMatchThreshold";

	@Key( ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.3" )
	double elementMatchThreshold();

	static final String ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY = "de.retest.recheck.rootElementMatchThreshold";

	@Key( ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.8" )
	double rootElementMatchThreshold();

	static final String ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY_KEY =
			"de.retest.recheck.rootElementContainedChildrenMatchThreshold";

	@Key( ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.5" )
	double rootElementContainedChildrenMatchThreshold();

	static final String REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY = "de.retest.recheck.rehub.reportUploadEnabled";

	@Key( REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY )
	@DefaultValue( "false" )
	boolean rehubReportUploadEnabled();

	static final String FILE_OUTPUT_FORMAT_PROPERTY_KEY = "de.retest.output.Format";

	@Key( FILE_OUTPUT_FORMAT_PROPERTY_KEY )
	FileOutputFormat fileOutputFormat();

	default FileOutputFormat getReportOutputFormat() {
		if ( rehubReportUploadEnabled() ) {
			return FileOutputFormat.CLOUD;
		}
		final FileOutputFormat format = fileOutputFormat();
		if ( format == null ) {
			return FileOutputFormat.KRYO;
		}
		return format;
	}

	default FileOutputFormat getStateOutputFormat() {
		final FileOutputFormat format = fileOutputFormat();
		return format == FileOutputFormat.ZIP ? format : FileOutputFormat.PLAIN;
	}

}
