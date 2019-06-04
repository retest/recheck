package de.retest.recheck.review.ignore.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ErrorHandlingLoader implements Loader<Void> {

	private static final Logger logger = LoggerFactory.getLogger( ErrorHandlingLoader.class );

	@Override
	public boolean canLoad( final String line ) {
		return true;
	}

	@Override
	public Void load( final String line ) {
		if ( line.trim().startsWith( "attribute:" ) ) {
			logger.error(
					"For ignoring an attribute globally, please use 'attribute=' (to ensure weired line breaks do not break your ignore file)." );
		} else {
			logger.error( "No loader defined for line '{}'. Read more at https://bit.ly/2QMxxO2", line );
		}
		return null;
	}

	@Override
	public String save( final Void ignore ) {
		return null;
	}

}
