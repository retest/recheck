package de.retest.recheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger( LoggingUncaughtExceptionHandler.class );

	@Override
	public void uncaughtException( final Thread t, final Throwable e ) {
		logger.error( "Exception in thread {}: ", t, e );
	}

}
