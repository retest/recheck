package de.retest.recheck.ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public static GlobalIgnoreApplier loadRecheckIgnore() {
		return loadRecheckSuiteIgnore( new LoadFilterWorker( NopCounter.getInstance() ) );
	}

	public static GlobalIgnoreApplier loadRecheckSuiteIgnore( final File ignoreFilesBasePath ) {
		return loadRecheckSuiteIgnore( new LoadFilterWorker( NopCounter.getInstance(), ignoreFilesBasePath.toPath() ) );
	}

	public static GlobalIgnoreApplier loadRecheckUserIgnore() {
		return loadRecheckSuiteIgnore( new LoadFilterWorker( NopCounter.getInstance(),
				Paths.get( System.getProperty( "user.home" ), ".retest" ) ) );
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
