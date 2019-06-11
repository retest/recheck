package de.retest.recheck.ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.configuration.ProjectConfigurationUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.review.workers.LoadFilterWorker;

public class RecheckIgnoreUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckIgnoreUtil.class );

	private RecheckIgnoreUtil() {}

	public static Optional<Path> getIgnoreFile() {
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE );
	}

	public static Optional<Path> getIgnoreRuleFile() {
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE_JSRULES );
	}

	private static Optional<Path> getRetestFile( final String filename ) {
		final Optional<Path> projectConfigurationFolder = ProjectConfigurationUtil.findProjectConfigurationFolder();
		return projectConfigurationFolder.map( p -> p.resolve( filename ) );
	}

	public static GlobalIgnoreApplier loadGlobalRecheckIgnore() {
		try {
			final LoadFilterWorker loadFilterWorker = new LoadFilterWorker( NopCounter.getInstance() );
			return loadFilterWorker.load();
		} catch ( final FileNotFoundException e ) {
			logger.error( "Could not find recheck ignore file." );
		} catch ( final Exception e ) {
			logger.error( "Exception loading recheck ignore file.", e );
		}
		return GlobalIgnoreApplier.create( NopCounter.getInstance() );
	}

	/**
	 * Loads a filter from the argument base path, e.g., test suite and step filters.
	 */
	public static GlobalIgnoreApplier loadRecheckIgnore( final File ignoreFilesBasePath ) {
		try {
			final LoadFilterWorker loadFilterWorker =
					new LoadFilterWorker( NopCounter.getInstance(), ignoreFilesBasePath );
			return loadFilterWorker.load();
		} catch ( final FileNotFoundException e ) {
			logger.debug( "Ignoring missing suite or step ignore file in '{}'.", ignoreFilesBasePath );
		} catch ( final Exception e ) {
			logger.error( "Exception loading suite or step ignore file.", e );
		}
		return GlobalIgnoreApplier.create( NopCounter.getInstance() );
	}
}
