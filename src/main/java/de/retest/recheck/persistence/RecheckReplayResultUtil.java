package de.retest.recheck.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.persistence.Persistence;
import de.retest.persistence.bin.KryoPersistence;
import de.retest.report.ReplayResult;
import de.retest.report.SuiteReplayResult;
import de.retest.util.FileUtil;

public class RecheckReplayResultUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckReplayResultUtil.class );

	private static final Persistence<ReplayResult> persistence = new KryoPersistence<>();

	public static void persist( final SuiteReplayResult suite, final File file ) {
		logger.info( "Persisting suite result to file {}.", FileUtil.canonicalPathQuietly( file ) );
		try {
			persistence.save( file.toURI(), ReplayResult.fromApi( suite ) );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not save replay result.", e );
		}
	}
}
