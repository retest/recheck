package de.retest.recheck;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Optional;

public class TestCaseFinder {

	private static final Set<String> testCaseAnnotations = new HashSet<String>( Arrays.asList( //
			"org.junit.Test", // JUnit Vintage (v4)
			"org.junit.jupiter.api.Test", // JUnit Jupiter (v5)
			"org.testng.annotations.Test" ) ); // TestNG

	public static StackTraceElement findTestCaseMethodInStack() {
		for ( final StackTraceElement[] stack : Thread.getAllStackTraces().values() ) {
			final Optional<StackTraceElement> testCaseStackElement = findTestCaseMethodInStack( stack );
			if ( testCaseStackElement.isPresent() ) {
				return testCaseStackElement.get();
			}
		}
		return null;
	}

	private static Optional<StackTraceElement> findTestCaseMethodInStack( final StackTraceElement[] trace ) {
		boolean inTestCase = false;
		for ( int i = 0; i < trace.length; i++ ) {
			if ( isTestCase( trace[i] ) ) {
				inTestCase = true;
			} else if ( inTestCase ) {
				return Optional.of( trace[i - 1] );
			}
		}
		return Optional.absent();
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
		Class<?> clazz;
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
