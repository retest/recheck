package de.retest.recheck;

import java.util.List;

import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigCache;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Reloadable;

import de.retest.recheck.configuration.ProjectRootFinderUtil;
import de.retest.recheck.persistence.FileOutputFormat;

@LoadPolicy( LoadType.MERGE )
@Sources( { "system:properties", "file:${projectroot}/.retest/retest.properties" } )
public interface RecheckProperties extends Reloadable {

	/*
	 * Basic usage.
	 */

	static void init() {
		ProjectRootFinderUtil.getProjectRoot().ifPresent(
				projectRoot -> ConfigFactory.setProperty( "projectroot", projectRoot.toAbsolutePath().toString() ) );
		ConfigCache.clear();
	}

	static RecheckProperties getInstance() {
		final RecheckProperties instance = ConfigCache.getOrCreate( RecheckProperties.class );
		instance.reload();
		return instance;
	}

	/*
	 * Various constants.
	 */

	String PROPERTY_VALUE_SEPARATOR = ";";
	String ZIP_FOLDER_SEPARATOR = "/";

	String SCREENSHOT_FOLDER_NAME = "screenshot";
	String RECHECK_FOLDER_NAME = "recheck";
	String DEFAULT_XML_FILE_NAME = "retest.xml";

	String RETEST_FOLDER_NAME = ".retest";

	String GOLDEN_MASTER_FILE_EXTENSION = ".recheck";
	String TEST_REPORT_FILE_EXTENSION = ".report";
	String AGGREGATED_TEST_REPORT_FILE_NAME = "tests" + TEST_REPORT_FILE_EXTENSION;

	/*
	 * Properties, their key constants and related functionality.
	 */

	String IGNORE_ATTRIBUTES_PROPERTY_KEY = "de.retest.recheck.ignore.attributes";

	@Key( IGNORE_ATTRIBUTES_PROPERTY_KEY )
	@DefaultValue( "" )
	@Separator( PROPERTY_VALUE_SEPARATOR )
	List<String> ignoreAttributes();

	String ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY = "de.retest.recheck.elementMatchThreshold";

	@Key( ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.3" )
	double elementMatchThreshold();

	String ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY = "de.retest.recheck.rootElementMatchThreshold";

	@Key( ROOT_ELEMENT_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.5" )
	double rootElementMatchThreshold();

	String ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY_KEY =
			"de.retest.recheck.rootElementContainedChildrenMatchThreshold";

	@Key( ROOT_ELEMENT_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY_KEY )
	@DefaultValue( "0.5" )
	double rootElementContainedChildrenMatchThreshold();

	String REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY = "de.retest.recheck.rehub.reportUploadEnabled";

	@Key( REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY )
	@DefaultValue( "false" )
	boolean rehubReportUploadEnabled();

	String REHUB_REPORT_AUTH_URL_PROPERTY_KEY = "de.retest.recheck.rehub.authUrl";

	@Key( REHUB_REPORT_AUTH_URL_PROPERTY_KEY )
	@DefaultValue( "https://login.retest.de/auth/realms/customer/protocol/openid-connect" )
	String rehubReportAuthUrl();

	String REHUB_REPORT_AUTH_KEY_PROPERTY_KEY = "de.retest.recheck.rehub.authKey";

	@Key( REHUB_REPORT_AUTH_KEY_PROPERTY_KEY )
	@DefaultValue( "tvrdxLHWA3h3SQRElQnGpKqxcCmMBpMSOhg4F7huUto" )
	String rehubAuthKey();

	String REHUB_REPORT_UPLOAD_URL_PROPERTY_KEY = "de.retest.recheck.rehub.reportUploadUrl";

	@Key( REHUB_REPORT_UPLOAD_URL_PROPERTY_KEY )
	@DefaultValue( "https://artifact-storage-steward.prod.cloud.retest.org/api/v1/report" )
	String rehubReportUploadUrl();

	String REHUB_REPORT_UPLOAD_ATTEMPTS = "de.retest.recheck.rehub.upload.attempts";

	@Key( REHUB_REPORT_UPLOAD_ATTEMPTS )
	@DefaultValue( "3" )
	int rehubReportUploadAttempts();

	String FILE_OUTPUT_FORMAT_PROPERTY_KEY = "de.retest.output.Format";

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
