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

public class GoldenMasterProviderImpl implements GoldenMasterProvider {

	public static final String RECHECK_PROJECT_ROOT = "de.retest.recheck.root";

	private static final Logger logger = LoggerFactory.getLogger( GoldenMasterProviderImpl.class );

	private final Persistence<SutState> persistence;

	public GoldenMasterProviderImpl( final Persistence<SutState> persistence ) {
		this.persistence = persistence;
	}

	@Override
	public File getGoldenMaster( final String filePath ) throws NoGoldenMasterFoundException {
		return ProjectRootFinderUtil.getProjectRoot() //
				.map( projectRoot -> getStates( projectRoot, filePath ) ) //
				.map( Path::toFile ) //
				.orElseThrow( () -> new NoGoldenMasterFoundException( filePath ) );
	}

	private Path getStates( final Path projectRoot, final String filePath ) {
		if ( projectRoot != null && filePath != null ) {
			logger.debug( "Looking for Golden Master files in '{}'.", projectRoot );
			final Path statePath = Paths.get( projectRoot.toAbsolutePath().toString(), filePath );
			if ( statePath.toFile().exists() ) {
				return statePath.toAbsolutePath();
			}
		}
		return null;
	}

	@Override
	public SutState loadGoldenMaster( final File file ) {
		final SutState result;
		try {
			result = persistence.load( file.toURI() );
		} catch ( final IOException e ) {
			throw new UncheckedIOException(
					"Could not load Golden Master from file '" + canonicalPathQuietly( file ) + "'.", e );
		}
		if ( result == null ) {
			throw new NullPointerException(
					"Loaded Golden Master from file '" + canonicalPathQuietly( file ) + "' is null." );
		}
		return result;
	}

	@Override
	public void saveGoldenMaster( final File file, final SutState state ) {
		final String canonicalPathQuietly = canonicalPathQuietly( file );
		try {
			persistence.save( file.toURI(), state );
			logger.info( "Updated Golden Master '{}'.", canonicalPathQuietly );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not apply changes to Golden Master '" + canonicalPathQuietly + "'.",
					e );
		}
	}

}
