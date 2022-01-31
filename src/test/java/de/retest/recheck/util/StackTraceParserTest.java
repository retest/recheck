package de.retest.recheck.util;

import static de.retest.recheck.util.StackTraceParser.parseStackTrace;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class StackTraceParserTest {

	private static final String AT = "\tat ";
	private static final String BR = System.getProperty( "line.separator" );

	private static String generate_$( final String msg ) {
		final Exception exception = new RuntimeException( msg );
		exception.fillInStackTrace();
		exception.setStackTrace(
				new StackTraceElement[] { exception.getStackTrace()[0], exception.getStackTrace()[1] } );
		final StringWriter writer = new StringWriter();
		exception.printStackTrace( new PrintWriter( writer ) );
		return writer.getBuffer().toString();
	}

	@Test
	public void should_parse_real_stacktrace() {
		final String stackTrace = generate_$( null );
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( stackTrace );
		assertThat( AT + parsed[0].toString() ).isEqualTo( stackTrace.split( BR )[1] );
	}

	@Test
	public void should_parse_$_signs_in_class() {
		final String stackTrace = "org.mortbay.io.nio.SelectorManager$SelectSet.doSelect(SelectorManager.java:468)";
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( AT + stackTrace + BR );
		assertThat( parsed[0].toString() ).isEqualTo( stackTrace );
	}

	@Test
	public void should_parse_Unknown_Source_signs_in_class() {
		final String stackTrace = "sun.nio.ch.SelectorImpl.select(Unknown Source)";
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( AT + stackTrace + BR );
		assertThat( parsed[0].toString() ).isEqualTo( stackTrace );
	}

	@Test
	public void should_parse_Native_Method_signs_in_class() {
		final String stackTrace = "sun.nio.ch.SelectorImpl.select(Native Method)";
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( AT + stackTrace + BR );
		assertThat( parsed[0].toString() ).isEqualTo( stackTrace );
	}

	@Test
	public void should_parse_numbers_in_method() {
		final String stackTrace = "sun.nio.ch.WindowsSelectorImpl$SubSelector.poll0(WindowsSelectorImpl.java:814)";
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( AT + stackTrace + BR );
		assertThat( parsed[0].toString() ).isEqualTo( stackTrace );
	}

	@Test
	public void should_parse_numbers_and_$() {
		final String stackTrace = "sun.nio.ch.WindowsSelectorImpl$1.access$400(WindowsSelectorImpl.java:1112)";
		final StackTraceElement[] parsed = StackTraceParser.parseStackTrace( AT + stackTrace + BR );
		assertThat( parsed[0].toString() ).isEqualTo( stackTrace );
	}

	@Test
	public void should_parse_filename() throws Exception {
		//	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
		final StackTraceElement element = new StackTraceElement( "sun.reflect.NativeMethodAccessorImpl", "invoke",
				"NativeMethodAccessorImpl.java", 39 );
		assertThat( parseStackTrace( AT + element.toString() )[0] ).isEqualTo( element );
	}
}
