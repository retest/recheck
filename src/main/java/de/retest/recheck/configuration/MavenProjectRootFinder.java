package de.retest.recheck.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenProjectRootFinder implements ProjectRootFinder {

	private static final Logger logger = LoggerFactory.getLogger( ProjectRootFinder.class );
	private final Set<Path> indicators = new HashSet<>();

	public MavenProjectRootFinder() {
		indicators.add( Paths.get( "src/main/java" ) );
		indicators.add( Paths.get( "src/test/java" ) );
	}

	@Override
	public Optional<Path> findProjectRoot() {
		return findProjectRoot( Paths.get( "" ).toAbsolutePath() );
	}

	@Override
	public Optional<Path> findProjectRoot( final Path basePath ) {
		if ( basePath == null || !basePath.toFile().exists() ) {
			logger.error( "Project root not found, base path does not exist." );
			return Optional.empty();
		}

		logger.debug( "Searching for project root under {}.", basePath );

		for ( Path currentFolder = basePath; currentFolder != null; currentFolder = currentFolder.getParent() ) {
			if ( containsSubPath( currentFolder ) ) {
				logger.debug( "Found project root in {}.", currentFolder );
				return Optional.of( currentFolder );
			}
		}

		logger.error( "Project root not found in {} or any parent folder.", basePath );
		return Optional.empty();
	}

	private boolean containsSubPath( final Path basePath ) {
		if ( basePath != null ) {
			return indicators.stream().anyMatch( subPath -> basePath.resolve( subPath ).toFile().exists() );
		}
		return false;
	}

}
