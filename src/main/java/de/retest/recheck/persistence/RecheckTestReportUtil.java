package de.retest.recheck.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.Properties;
import de.retest.recheck.SuiteAggregator;
import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.util.FileUtil;

public class RecheckTestReportUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckTestReportUtil.class );

	private static final Persistence<TestReport> persistence = new KryoPersistence<>();

	private RecheckTestReportUtil() {}

	public static void persist( final SuiteReplayResult suite, final File file ) {
		logger.info( "Persisting test report to file '{}'.", FileUtil.canonicalPathQuietly( file ) );
		try {
			FileUtil.ensureFolder( file );

			// Save separate test report for suite.
			persistence.save( file.toURI(), TestReport.fromApi( suite ) );

			// Save/update aggregated test report for all suites.
			final File testReportFile = new File( file.getParent(),
					Properties.DEFAULT_REPORT_FILE + Properties.TEST_REPORT_FILE_EXTENSION );
			final TestReport testReport = SuiteAggregator.getInstance().getTestReport();
			persistence.save( testReportFile.toURI(), testReport );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save test report.", e );
		}
	}
}
