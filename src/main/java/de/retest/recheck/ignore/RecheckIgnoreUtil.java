package de.retest.recheck.ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.review.workers.LoadFilterWorker;

/**
 * @deprecated Use {@link RecheckIgnoreLocator} instead.
 */
// TODO Remove with recheck 2.0
@Deprecated
public class RecheckIgnoreUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckIgnoreUtil.class );

	private RecheckIgnoreUtil() {}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getProjectIgnoreFile()} instead.
	 */
	@Deprecated
	public static Optional<Path> getProjectIgnoreFile( final String filename ) {
		return new RecheckIgnoreLocator( filename ).getProjectIgnoreFile();
	}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getUserIgnoreFile()} instead.
	 */
	@Deprecated
	public static Path getUserIgnoreFile( final String filename ) {
		return new RecheckIgnoreLocator( filename ).getUserIgnoreFile();
	}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getSuiteIgnoreFile(Path)} instead.
	 */
	@Deprecated
	public static Path getSuiteIgnoreFile( final String filename, final Path basePath ) {
		return new RecheckIgnoreLocator( filename ).getSuiteIgnoreFile( basePath );
	}

	// It appears this method is not used anywhere in downstream projects nor elsewhere. Marked for deletion.
	@Deprecated
	public static GlobalIgnoreApplier loadRecheckIgnore( final File suiteIgnorePath ) {
		return loadRecheckSuiteIgnore( new LoadFilterWorker( NopCounter.getInstance(), suiteIgnorePath.toPath() ) );
	}

	private static GlobalIgnoreApplier loadRecheckSuiteIgnore( final LoadFilterWorker loadFilterWorker ) {
		try {
			return loadFilterWorker.load();
		} catch ( final NoSuchFileException | FileNotFoundException e ) {
			logger.debug( "Ignoring missing suite or user ignore file." );
		} catch ( final Exception e ) {
			logger.error( "Exception loading suite or user ignore file.", e );
		}
		return GlobalIgnoreApplier.create( NopCounter.getInstance() );
	}
}
