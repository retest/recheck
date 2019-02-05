package de.retest.recheck.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.retest.recheck.ui.actions.ExceptionWrapper;

public class ExceptionUtil {

	private static final Pattern excNamePattern = Pattern.compile( "([\\w\\/.]+Exception|Error)" );
	private static final String STACKTRACE_START_PATTERN = "\tat ";
	private static final String CAUSE_START_PATTERN = "Caused by: ";

	public static String getStackTrace( final Throwable exc ) {
		if ( exc == null ) {
			return "";
		}
		final StringWriter errors = new StringWriter();
		exc.printStackTrace( new PrintWriter( errors ) );
		return errors.toString();
	}

	public static List<String> getStackTraceAsList( final Throwable exc ) {
		final List<String> result = new ArrayList<>();
		for ( final StackTraceElement element : exc.getStackTrace() ) {
			result.add( element.toString() );
		}
		return result;
	}

	public static Throwable getDeepestCause( final Throwable throwable ) {
		if ( throwable == null ) {
			return null;
		}
		if ( throwable.getCause() != null ) {
			return getDeepestCause( throwable.getCause() );
		}
		return throwable;
	}

	public static ExceptionWrapper reconstructThrowableFromLog( final String text ) {
		// simpler and faster than pattern matching
		if ( !text.contains( "Exception" ) && !text.contains( "Error" ) ) {
			return null;
		}
		final String deepestCause = clipToDeepestCause( text );
		final Matcher matcher = excNamePattern.matcher( deepestCause );
		if ( !matcher.find() ) {
			return null;
		}
		final String type = matcher.group( 1 );
		final String[] stack = deepestCause.split( "\n" );
		String errorMsg = stack[0].replace( type, "" );
		if ( errorMsg.startsWith( CAUSE_START_PATTERN ) ) {
			errorMsg = errorMsg.replace( CAUSE_START_PATTERN, "" );
		}
		if ( errorMsg.startsWith( ": " ) ) {
			errorMsg = errorMsg.replace( ": ", "" );
		}
		return new ExceptionWrapper( type, text, errorMsg,
				stack.length > 1 ? stack[1].replace( STACKTRACE_START_PATTERN, "" ) : null );
	}

	public static Throwable reconstructThrowableFromWrapper( final ExceptionWrapper error ) {
		Throwable result = reconstructInstance( error.getType(), error.getMessage() );
		if ( result == null ) {
			result = new RuntimeException( error.getType() + ": " + error.getMessage() );
		}
		result.setStackTrace( reconstructStackTrace( error.getStackTrace() ) );
		return result;
	}

	protected static StackTraceElement[] reconstructStackTrace( final String stackTrace ) {
		if ( stackTrace == null ) {
			return new StackTraceElement[0];
		}
		return StackTraceParser.parseStackTrace( stackTrace );
	}

	protected static Throwable reconstructInstance( final String type, final String msg ) {
		if ( type == null ) {
			return null;
		}
		try {
			@SuppressWarnings( "unchecked" )
			final Class<Throwable> clazz = (Class<Throwable>) Class.forName( type );
			try {
				return clazz.getConstructor( String.class ).newInstance( msg );
			} catch ( final Exception exc ) {
				return clazz.newInstance();
			}
		} catch ( final Exception exc ) {
			// ignore
		}
		return null;
	}

	private static String clipToDeepestCause( final String text ) {
		if ( !text.contains( CAUSE_START_PATTERN ) ) {
			return text;
		}
		return text.substring( text.lastIndexOf( CAUSE_START_PATTERN ) );
	}

	public static boolean containsError( final String text ) {
		// simpler and faster than pattern matching
		if ( !text.contains( "Exception" ) && !text.contains( "Error" ) ) {
			return false;
		}
		final Matcher matcher = excNamePattern.matcher( text );
		if ( !matcher.find() ) {
			return false;
		}
		return true;
	}

	public static boolean isPartOfPrintStackTrace( final String msg ) {
		return msg.contains( STACKTRACE_START_PATTERN ) || msg.contains( CAUSE_START_PATTERN );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getExceptionInChain( final Throwable exc, final Class<T> clazz ) {
		Throwable cause = exc;
		while ( cause != null ) {
			if ( clazz.isAssignableFrom( cause.getClass() ) ) {
				return (T) cause;
			}
			cause = cause.getCause();
		}
		return null;
	}

	public static boolean hasExceptionInChain( final Throwable exc, final Class<?> clazz ) {
		return getExceptionInChain( exc, clazz ) != null;
	}

	public static String getDeepestStackTraceElement( final Throwable throwable ) {
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		if ( stackTrace == null ) {
			return null;
		}
		return stackTrace[0].toString();
	}

	public static List<String> getExceptionStackTraceCompletely( final Throwable t ) {
		final List<String> stackTrace = new ArrayList<>();
		stackTrace.add( t.toString() );
		Arrays.stream( t.getStackTrace() ).map( ste -> "\tat " + ste ).forEach( stackTrace::add );
		final Throwable cause = t.getCause();
		if ( cause != null ) {
			final List<String> stackTraceCause = getExceptionStackTraceCompletely( cause );
			stackTraceCause.set( 0, "Caused by: " + stackTraceCause.get( 0 ) );
			stackTrace.addAll( stackTraceCause );
		}
		return stackTrace;
	}
}
