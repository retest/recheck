package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.util.FileUtil;

public class RecheckTestReportUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckTestReportUtil.class );

	private static final PersistenceFactory persistenceFactory = new PersistenceFactory( getXmlTransformer() );

	private RecheckTestReportUtil() {
	}

	public static void persist( final SuiteReplayResult suite, final File file ) {
		logger.info( "Persisting test report to file '{}'.", FileUtil.canonicalPathQuietly( file ) );
		try {
			FileUtil.ensureFolder( file );
			persistenceFactory.getPersistence().save( file.toURI(), TestReport.fromApi( suite ) );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save test report.", e );
		}
	}
}
