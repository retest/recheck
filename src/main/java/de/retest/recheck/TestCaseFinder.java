package de.retest.recheck;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestCaseFinder {

	private TestCaseFinder() {}

	/*
	 * TODO We need a special implementation for data-driven testing with annotations such as JUnit's @Theory, because
	 * then a single method is invoked multiple times.
	 */
	private static final Set<String> testCaseAnnotations = new HashSet<>( Arrays.asList( //
			// JUnit Vintage (v4)
			"org.junit.Test", //
			"org.junit.Before", //
			"org.junit.After", //
			"org.junit.BeforeClass", //
			"org.junit.AfterClass", //
			// JUnit Jupiter (v5)
			"org.junit.jupiter.api.Test", //
			"org.junit.jupiter.api.BeforeEach", //
			"org.junit.jupiter.api.AfterEach", //
			"org.junit.jupiter.api.BeforeAll", //
			"org.junit.jupiter.api.AfterAll", //
			"org.junit.jupiter.params.ParameterizedTest", //
			// TestNG
			"org.testng.annotations.Test", //
			"org.testng.annotations.BeforeMethod", //
			"org.testng.annotations.AfterMethod", //
			"org.testng.annotations.BeforeClass", //
			"org.testng.annotations.AfterClass" ) );

	public static StackTraceElement findTestCaseMethodInStack() {
		for ( final StackTraceElement[] stack : Thread.getAllStackTraces().values() ) {
			final StackTraceElement testCaseStackElement = findTestCaseMethodInStack( stack );
			if ( testCaseStackElement != null ) {
				return testCaseStackElement;
			}
		}
		return null;
	}

	public static StackTraceElement findTestCaseMethodInStack( final StackTraceElement[] trace ) {
		boolean inTestCase = false;
		for ( int i = 0; i < trace.length; i++ ) {
			if ( isTestCase( trace[i] ) ) {
				inTestCase = true;
			} else if ( inTestCase ) {
				return trace[i - 1];
			}
		}
		return null;
	}

	private static boolean isTestCase( final StackTraceElement element ) {
		final Method method = tryToFindMethodForStackTraceElement( element );
		if ( method == null ) {
			return false;
		}

		final Annotation[] annotations = method.getAnnotations();
		for ( final Annotation annotation : annotations ) {
			final String annotationName = annotation.annotationType().getName();
			if ( testCaseAnnotations.contains( annotationName ) ) {
				return true;
			}
		}

		return false;
	}

	private static Method tryToFindMethodForStackTraceElement( final StackTraceElement element ) {
		final Class<?> clazz;
		Method method = null;

		try {
			clazz = Class.forName( element.getClassName() );
		} catch ( final ClassNotFoundException e ) {
			return null;
		}

		for ( final Method methodCandidate : clazz.getDeclaredMethods() ) {
			if ( methodCandidate.getName().equals( element.getMethodName() ) ) {
				if ( method == null ) {
					method = methodCandidate;
				} else {
					// two methods with same name found, can't determine correct one!
					return null;
				}
			}
		}
		return method;
	}

}
