package de.retest.file;

import static de.retest.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.retest.Properties;
import de.retest.configuration.Configuration;
import de.retest.util.FileUtil;

public class ReportFileUtils {

	public static final String REPORT_FILE_EXTENSION = ".result";
	public static final String REPORT_FILE_DEFAULT_FILE_NAME = "replay" + REPORT_FILE_EXTENSION;

	private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm";

	public static File deleteLatestAndCreateReportDir() {
		final File latest = getLatestReportDir();
		if ( latest.exists() ) {
			FileUtil.deleteRecursively( latest );
		}
		final File reportDir =
				new File( getBaseDir(), "report-" + new SimpleDateFormat( DATE_FORMAT ).format( new Date() ) );
		checkReportDir( reportDir );
		return reportDir;
	}

	public static File getLatestReportDir() {
		return new File( getBaseDir(), "latest" );
	}

	public static File getLatestReport() throws FileNotFoundException {
		final File report = new File( getLatestReportDir(), REPORT_FILE_DEFAULT_FILE_NAME );
		if ( report.exists() ) {
			return report;
		}
		throw new FileNotFoundException( "Default report file '" + REPORT_FILE_DEFAULT_FILE_NAME
				+ "' doesn't exist in '" + getLatestReportDir() + "'." );
	}

	private static void checkReportDir( final File reportDir ) {
		reportDir.mkdirs();
		if ( !reportDir.mkdir() && !reportDir.exists() ) {
			throw new RuntimeException( "Unable to create dir for test report: " + canonicalPathQuietly( reportDir ) );
		}
		if ( !reportDir.canWrite() ) {
			throw new RuntimeException( "Unable to write test report into dir: " + canonicalPathQuietly( reportDir ) );
		}
	}

	public static File getBaseDir() {
		final String testresultsDirProperty = System.getProperty( Properties.TESTRESULTS_REPORTSDIR );
		File baseDir = null;
		if ( testresultsDirProperty == null ) {
			baseDir = new File( Configuration.getRetestWorkspace(), Properties.TESTRESULTS_REPORTSDIR_DEFAULT );
		} else {
			baseDir = new File( testresultsDirProperty );
		}
		return baseDir;
	}

	public static File getActionDir( final File reportDir, final int testNr, final int actionNr ) {
		final File actionDir = new File( new File( reportDir, "test_" + testNr ), "action_" + actionNr );
		actionDir.mkdirs();
		return actionDir;
	}

	public static void updateLatest( final File report ) {
		final File latest = getLatestReportDir();
		copyLatest( report, latest );
	}

	private static void copyLatest( final File report, final File latest ) {
		try {
			FileUtil.copy( report, latest );
		} catch ( final IOException e ) {
			throw new RuntimeException(
					"Exception copying report '" + canonicalPathQuietly( report ) + "' to 'latest'.", e );
		}
	}

}
