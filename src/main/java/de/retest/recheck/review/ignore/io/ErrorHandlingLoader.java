package de.retest.recheck.review.ignore.io;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;

public final class ErrorHandlingLoader implements Loader<FilterPreserveLine> {

	private static final Logger logger = LoggerFactory.getLogger( ErrorHandlingLoader.class );

	@Override
	public Optional<FilterPreserveLine> load( final String line ) {
		if ( line.startsWith( "attribute:" ) ) {
			logger.error(
					"For ignoring an attribute globally, please use 'attribute=' (to ensure weired line breaks do not break your ignore file)." );
		} else {
			logger.error(
					"No loader defined for line '{}'. Read more at https://docs.retest.de/recheck/how-ignore-works/",
					line );
		}
		return Optional.of( new FilterPreserveLine( line ) );
	}

	@Override
	public String save( final FilterPreserveLine errorLine ) {
		return errorLine.toString();
	}

}
