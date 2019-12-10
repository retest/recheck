package de.retest.recheck.ignore;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfigurationUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.review.workers.LoadFilterWorker;

public class RecheckIgnoreUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckIgnoreUtil.class );

	private RecheckIgnoreUtil() {}

	public static Optional<Path> getProjectIgnoreFile( final String filename ) {
		final Optional<Path> projectConfigurationFolder = ProjectConfigurationUtil.findProjectConfigurationFolder();
		return projectConfigurationFolder.map( p -> p.resolve( filename ) );
	}

	public static Path getUserIgnoreFile( final String filename ) {
		return Paths.get( System.getProperty( "user.home" ) ).resolve( RETEST_FOLDER_NAME ).resolve( filename );
	}

	public static Path getSuiteIgnoreFile( final String filename, final Path basePath ) {
		return basePath.resolve( filename );
	}

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
