package de.retest.recheck.persistence.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.logging.LogUtil;
import de.retest.recheck.persistence.xml.util.XmlVersionCheckResult;
import de.retest.recheck.util.NamedBufferedInputStream;
import de.retest.recheck.util.ThreadUtil;

public class XmlMigrator {

	private XmlMigrator() {}

	private static final Logger logger = LoggerFactory.getLogger( XmlMigrator.class );

	public static NamedBufferedInputStream tryToMigrate( final XmlVersionCheckResult checkResult,
			final NamedBufferedInputStream bin ) {

		final XmlMigratorInstances migrator = XmlMigratorInstances.get( checkResult.oldDataType );
		InputStream finalInputStream = bin;

		if ( migrator != null ) {
			if ( !ThreadUtil.stackTraceContainsClass( "de.retest.migration.TestMigrator" )
					&& !ThreadUtil.stackTraceContainsClass( "de.retest.TestMigrator" ) ) {
				logger.warn( LogUtil.LOG_SEPARATOR );
				logger.warn( "ReTest detected an old version of the file '{}'.", bin.getName() );
				logger.warn(
						"ReTest migrates the file now, but will discard the results to avoid VCS file conflicts." );
				logger.warn(
						"It is very recommended to locally convert the file and commit it to speed up execution!" );
				logger.warn( LogUtil.LOG_SEPARATOR );
			}

			logger.info( "Migrating file '{}' from version {} to version {}. This may take a while.", bin.getName(),
					checkResult.oldVersion, checkResult.newDataTypeInstance.version() );
			final List<XmlTransformer> transformers = new ArrayList<>();

			// TODO: See RET-797 This is not optimal! When transforming XML, the attribute dataTypeVersion should reflect the specific version instead of the final one.
			transformers.addAll( createRemoveDatatypeAndVersionTransformer( checkResult.oldDataType,
					Integer.toString( checkResult.newDataTypeInstance.version() ) ) );
			transformers.addAll( migrator.getXmlTransformersFor( checkResult.oldVersion ) );

			for ( int i = 0; i < transformers.size(); i++ ) {
				final boolean notFinalInputStream = i < transformers.size() - 1;
				final InputStream previousInputStream = finalInputStream;
				finalInputStream = transformers.get( i ).transform( finalInputStream );

				if ( notFinalInputStream ) {
					try {
						previousInputStream.close();
					} catch ( final IOException e ) {
						logger.error( "Error closing previous input stream during migration.", e );
					}
				}
			}

			logger.info( "Migrated file '{}' from version {} to version {}.", bin.getName(), checkResult.oldVersion,
					checkResult.newDataTypeInstance.version() );
			//			if debug is enabled, we still don't want an OutOfMemoryError...
			//			inputStream = writeMigrationResultToLogfile( inputStream );

			return new NamedBufferedInputStream( finalInputStream, bin.getName() );
		}
		return throwUnableToMigrateException( checkResult );
	}

	protected static List<XmlTransformer> createRemoveDatatypeAndVersionTransformer( final String dataType,
			final String dataTypeVersion ) {
		final List<XmlTransformer> result = new ArrayList<>();
		result.add( new ReplaceAttributeTransformer( "reTestXmlDataContainer", "dataType", ".*", dataType ) );
		result.add(
				new ReplaceAttributeTransformer( "reTestXmlDataContainer", "dataTypeVersion", ".*", dataTypeVersion ) );
		return result;
	}

	private static NamedBufferedInputStream throwUnableToMigrateException( final XmlVersionCheckResult checkResult ) {
		throw new RuntimeException( "Unexpected data version, version in XML file=" + checkResult.oldVersion
				+ ", version in the corresponding class=" + checkResult.newDataTypeInstance.version() );
	}
}
