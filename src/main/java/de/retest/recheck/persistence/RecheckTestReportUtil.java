package de.retest.recheck.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.persistence.bin.KryoPersistence;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.util.FileUtil;

public class RecheckTestReportUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckTestReportUtil.class );

	private static final Persistence<TestReport> persistence = new KryoPersistence<>();

	public static void persist( final SuiteReplayResult suite, final File file ) {
		logger.info( "Persisting test report to file '{}'.", FileUtil.canonicalPathQuietly( file ) );
		try {
			persistence.save( file.toURI(), TestReport.fromApi( suite ) );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save test report.", e );
		}
	}
}
