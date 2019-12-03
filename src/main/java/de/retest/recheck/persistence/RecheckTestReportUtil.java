package de.retest.recheck.persistence;

import static de.retest.recheck.XmlTransformerUtil.getXmlTransformer;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.RecheckProperties;
import de.retest.recheck.SuiteAggregator;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;

public class RecheckTestReportUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckTestReportUtil.class );

	private static final PersistenceFactory persistenceFactory = new PersistenceFactory( getXmlTransformer() );

	private RecheckTestReportUtil() {}

	public static void persist( final SuiteReplayResult suite, final File file ) {
		logger.info( "Persisting test report to file '{}'.", file.getAbsoluteFile().toURI() );
		try {
			// Save separate test report for suite.
			persistenceFactory.getPersistence().save( file.toURI(), TestReport.fromApi( suite ) );

			// Save/update aggregated test report for all suites.
			final File testReportFile =
					new File( file.getParent(), RecheckProperties.AGGREGATED_TEST_REPORT_FILE_NAME );
			final TestReport aggregatedTestReport = SuiteAggregator.getInstance().getAggregatedTestReport();
			persistenceFactory.getPersistence().save( testReportFile.toURI(), aggregatedTestReport );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save test report.", e );
		}
	}
}
