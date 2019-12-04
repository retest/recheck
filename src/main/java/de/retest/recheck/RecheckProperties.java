package de.retest.recheck;

import java.util.List;

import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigCache;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

import de.retest.recheck.configuration.ProjectRootFinderUtil;

@Sources( "file:${projectroot}/.retest/retest.properties" )
public interface RecheckProperties extends Mutable {

	// Usage.

	static void init() {
		ProjectRootFinderUtil.getProjectRoot().ifPresent(
				projectRoot -> ConfigFactory.setProperty( "projectroot", projectRoot.toAbsolutePath().toString() ) );
	}

	static RecheckProperties getInstance() {
		return ConfigCache.getOrCreate( RecheckProperties.class, System.getProperties() );
	}

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

	// Constants.

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

	// Properties.

	@Key( "de.retest.recheck.ignore.attributes" )
	@DefaultValue( "" )
	@Separator( PROPERTY_VALUE_SEPARATOR )
	List<String> ignoreAttributes();

	@Key( "de.retest.recheck.elementMatchThreshold" )
	@DefaultValue( "0.3" )
	double elementMatchThreshold();

	@Key( "de.retest.recheck.rootElementMatchThreshold" )
	@DefaultValue( "0.8" )
	double rootElementMatchThreshold();

	@Key( "de.retest.recheck.rootElementContainedChildrenMatchThreshold" )
	@DefaultValue( "0.5" )
	double rootElementContainedChildrenMatchThreshold();

	@Key( "de.retest.recheck.rehub.reportUploadEnabled" )
	@DefaultValue( "false" )
	boolean rehubReportUploadEnabled();

	@Key( "de.retest.output.Format" )
	FileOutputFormat fileOutputFormat();

}
