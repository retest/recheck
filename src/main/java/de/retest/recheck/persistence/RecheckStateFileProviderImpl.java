package de.retest.recheck.persistence;

import static de.retest.recheck.util.FileUtil.canonicalPathQuietly;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectRootFinderUtil;
import de.retest.recheck.ui.descriptors.SutState;

public class RecheckStateFileProviderImpl implements RecheckStateFileProvider {

	public static final String RECHECK_PROJECT_ROOT = "de.retest.recheck.root";

	private static final Logger logger = LoggerFactory.getLogger( RecheckStateFileProviderImpl.class );

	private final Persistence<SutState> persistence;

	public RecheckStateFileProviderImpl( final Persistence<SutState> persistence ) {
		this.persistence = persistence;
	}

	@Override
	public File getRecheckStateFile( final String filePath ) throws NoStateFileFoundException {
		final Path projectRoot = ProjectRootFinderUtil.getProjectRoot() //
				.orElseThrow( () -> new NoStateFileFoundException( filePath ) );
		final Path projectRootStates = getStates( projectRoot, filePath );

		if ( projectRootStates != null ) {
			return projectRootStates.toFile();
		}

		throw new NoStateFileFoundException( filePath );
	}

	private Path getStates( final Path projectRoot, final String filePath ) {
		if ( projectRoot != null && filePath != null ) {
			logger.debug( "Looking for SUT state files in '{}'.", projectRoot );
			final Path statePath = Paths.get( projectRoot.toAbsolutePath().toString(), filePath );
			if ( statePath.toFile().exists() ) {
				return statePath.toAbsolutePath();
			}
		}
		return null;
	}

	@Override
	public SutState loadRecheckState( final File file ) {
		final SutState result;
		try {
			result = persistence.load( file.toURI() );
		} catch ( final IOException e ) {
			throw new UncheckedIOException(
					"Could not load SUT state from file '" + canonicalPathQuietly( file ) + "'.", e );
		}
		if ( result == null ) {
			throw new NullPointerException(
					"Loaded SUT state from file " + canonicalPathQuietly( file ) + "' is null." );
		}
		return result;
	}

	@Override
	public void saveRecheckState( final File file, final SutState state ) {
		final String canonicalPathQuietly = canonicalPathQuietly( file );
		try {
			persistence.save( file.toURI(), state );
			logger.info( "Updated SUT state file {}.", canonicalPathQuietly );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not apply changes to SUT state file '" + canonicalPathQuietly + "'.",
					e );
		}
	}

}
