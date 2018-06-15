package de.retest.recheck;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.Theory;

import com.google.common.base.Optional;

/**
 * This class is a duplicate of class TestCaseFinder in testutils module.
 */
public class TestCaseFinder {

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
		return method != null && (method.isAnnotationPresent( Test.class ) || method.isAnnotationPresent( Theory.class )
				|| method.isAnnotationPresent( Before.class ) || method.isAnnotationPresent( After.class )
				|| method.isAnnotationPresent( BeforeClass.class ) || method.isAnnotationPresent( AfterClass.class ));
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
