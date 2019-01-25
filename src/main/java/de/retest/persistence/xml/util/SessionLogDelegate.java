package de.retest.persistence.xml.util;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.Session;

public class SessionLogDelegate implements SessionLog {

	private final SessionLog delegate;
	private final Writer writer = new StringWriter();

	public SessionLogDelegate( final SessionLog delegate ) {
		this.delegate = delegate;
		delegate.setWriter( writer );
	}

	@Override
	public void log( final SessionLogEntry entry ) {
		delegate.log( entry );
	}

	@Override
	public boolean shouldLogExceptionStackTrace() {
		return delegate.shouldLogExceptionStackTrace();
	}

	@Override
	public boolean shouldPrintDate() {
		return delegate.shouldPrintDate();
	}

	@Override
	public boolean shouldPrintThread() {
		return delegate.shouldPrintThread();
	}

	@Override
	public boolean shouldDisplayData() {
		return delegate.shouldDisplayData();
	}

	@Override
	public boolean shouldPrintConnection() {
		return delegate.shouldPrintConnection();
	}

	@Override
	public boolean shouldPrintSession() {
		return delegate.shouldPrintSession();
	}

	@Override
	public void setShouldDisplayData( final Boolean shouldDisplayData ) {
		delegate.setShouldDisplayData( shouldDisplayData );
	}

	@Override
	public void setShouldLogExceptionStackTrace( final boolean flag ) {
		delegate.setShouldLogExceptionStackTrace( flag );
	}

	@Override
	public void setShouldPrintDate( final boolean flag ) {
		delegate.setShouldPrintDate( flag );
	}

	@Override
	public void setShouldPrintThread( final boolean flag ) {
		delegate.setShouldPrintThread( flag );
	}

	@Override
	public void setShouldPrintConnection( final boolean flag ) {
		delegate.setShouldPrintConnection( flag );
	}

	@Override
	public void setShouldPrintSession( final boolean flag ) {
		delegate.setShouldPrintSession( flag );
	}

	@Override
	public Writer getWriter() {
		return delegate.getWriter();
	}

	@Override
	public void setWriter( final Writer log ) {
		delegate.setWriter( log );
	}

	@Override
	public int getLevel() {
		return delegate.getLevel();
	}

	@Override
	public String getLevelString() {
		return delegate.getLevelString();
	}

	@Override
	public int getLevel( final String category ) {
		return delegate.getLevel( category );
	}

	@Override
	public void setLevel( final int level ) {
		delegate.setLevel( level );
	}

	@Override
	public void setLevel( final int level, final String category ) {
		delegate.setLevel( level, category );
	}

	@Override
	public boolean shouldLog( final int level ) {
		return delegate.shouldLog( level );
	}

	@Override
	public boolean shouldLog( final int level, final String category ) {
		return delegate.shouldLog( level, category );
	}

	@Override
	public void log( final int level, final String message ) {
		delegate.log( level, message );
	}

	@Override
	public void log( final int level, final String message, final Object param ) {
		delegate.log( level, message, param );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object param ) {
		delegate.log( level, category, message, param );
	}

	@Override
	public void log( final int level, final String message, final Object param1, final Object param2 ) {
		delegate.log( level, message, param1, param2 );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object param1,
			final Object param2 ) {
		delegate.log( level, category, message, param1, param2 );
	}

	@Override
	public void log( final int level, final String message, final Object param1, final Object param2,
			final Object param3 ) {
		delegate.log( level, message, param1, param2, param3 );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object param1,
			final Object param2, final Object param3 ) {
		delegate.log( level, category, message, param1, param2, param3 );
	}

	@Override
	public void log( final int level, final String message, final Object param1, final Object param2,
			final Object param3, final Object param4 ) {
		delegate.log( level, message, param1, param2, param3, param4 );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object param1,
			final Object param2, final Object param3, final Object param4 ) {
		delegate.log( level, category, message, param1, param2, param3, param4 );
	}

	@Override
	public void log( final int level, final String message, final Object[] arguments ) {
		delegate.log( level, message, arguments );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object[] arguments ) {
		delegate.log( level, category, message, arguments );
	}

	@Override
	public void log( final int level, final String message, final Object[] arguments, final boolean shouldTranslate ) {
		delegate.log( level, message, arguments, shouldTranslate );
	}

	@Override
	public void log( final int level, final String category, final String message, final Object[] arguments,
			final boolean shouldTranslate ) {
		delegate.log( level, category, message, arguments, shouldTranslate );
	}

	@Override
	public void throwing( final Throwable throwable ) {
		delegate.throwing( throwable );
	}

	@Override
	public void severe( final String message ) {
		delegate.severe( message );
	}

	@Override
	public void warning( final String message ) {
		delegate.warning( message );
	}

	@Override
	public void info( final String message ) {
		delegate.info( message );
	}

	@Override
	public void config( final String message ) {
		delegate.config( message );
	}

	@Override
	public void fine( final String message ) {
		delegate.fine( message );
	}

	@Override
	public void finer( final String message ) {
		delegate.finer( message );
	}

	@Override
	public void finest( final String message ) {
		delegate.finest( message );
	}

	@Override
	public void logThrowable( final int level, final Throwable throwable ) {
		delegate.logThrowable( level, throwable );
	}

	@Override
	public void logThrowable( final int level, final String category, final Throwable throwable ) {
		delegate.logThrowable( level, category, throwable );
	}

	@Override
	public Session getSession() {
		return delegate.getSession();
	}

	@Override
	public void setSession( final Session session ) {
		delegate.setSession( session );
	}

	@Override
	public Object clone() {
		return delegate.clone();
	}

	public String getLog() {
		return writer.toString();
	}

	public boolean containsMessages() {
		return !writer.toString().trim().isEmpty();
	}
}
