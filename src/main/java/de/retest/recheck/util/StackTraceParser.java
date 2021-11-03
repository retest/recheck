package de.retest.recheck.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceParser {

	// improved on http://stackoverflow.com/a/10014651/58997
	public static StackTraceElement[] parseStackTrace( final String stackTrace ) {
		final Pattern headLinePattern = Pattern.compile( "([\\w\\.]+)(:.*)?" );
		final Matcher headLineMatcher = headLinePattern.matcher( stackTrace );
		if ( headLineMatcher.find() ) {
			headLineMatcher.group( 1 );
			if ( headLineMatcher.group( 2 ) != null ) {
				headLineMatcher.group( 2 );
			}
		}
		// "at package.class.method(source.java:123)"
		final Pattern tracePattern = Pattern.compile(
				"\\s*at\\s+([\\d\\w\\.$_]+)\\.([\\d\\w$_]+)\\((((.*java)?:(\\d+))|Native Method|Unknown Source)\\)(\\n|\\r\\n)?" );
		final Matcher traceMatcher = tracePattern.matcher( stackTrace );
		final List<StackTraceElement> result = new ArrayList<>();
		while ( traceMatcher.find() ) {
			final String className = traceMatcher.group( 1 );
			final String methodName = traceMatcher.group( 2 );
			final String sourceFile = traceMatcher.group( 5 );
			final String lineNumString = traceMatcher.group( 6 );
			int lineNum = -1;
			if ( lineNumString != null ) {
				lineNum = Integer.parseInt( lineNumString );
			} else if ( "Native Method".equals( traceMatcher.group( 3 ) ) ) {
				lineNum = -2;
			}
			result.add( new StackTraceElement( className, methodName, sourceFile, lineNum ) );
		}
		return result.toArray( new StackTraceElement[result.size()] );
	}
}
