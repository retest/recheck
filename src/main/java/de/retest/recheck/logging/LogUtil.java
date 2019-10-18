package de.retest.recheck.logging;

import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;

public class LogUtil {

	private LogUtil() {}

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( LogUtil.class );

	public static final String LOG_SEPARATOR =
			"********************************************************************************";

	public static void setDefaultLogLevel() {
		if ( !(logger instanceof ch.qos.logback.classic.Logger) ) {
			logger.info( "Setting default log level not implemented for logger of {}.", logger.getClass() );
			return;
		}
		if ( getLogbackConfigFile() == null ) {
			logger.warn(
					"No logback.xml found or configured! Please review your setting of property 'logback.configurationFile'." );
			final Logger root = (Logger) LoggerFactory.getLogger( org.slf4j.Logger.ROOT_LOGGER_NAME );
			root.setLevel( Level.INFO );
		}
	}

	public static URL getLogbackConfigFile() {
		final LoggerContext loggerContext = ((ch.qos.logback.classic.Logger) logger).getLoggerContext();
		final URL mainURL = ConfigurationWatchListUtil.getMainWatchURL( loggerContext );
		return mainURL;
	}
}
