package de.retest.recheck;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCaseFinder {

	/**
	 * Delimiter to separate methods names from their invocation count.
	 */
	public static final String DELIMITER = "_";

	private static final TestCaseInformation NO_TEST_CASE_INFORMATION =
			new TestCaseInformation( null, TestCaseAnnotationType.NONE, 0 );

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
			// TestNG
			"org.testng.annotations.Test", //
			"org.testng.annotations.BeforeMethod", //
			"org.testng.annotations.AfterMethod", //
			"org.testng.annotations.BeforeClass", //
			"org.testng.annotations.AfterClass" ) );

	private static final Set<String> repeatableTestCaseAnnotations = new HashSet<>( Arrays.asList( //
			// JUnit Jupiter (v5)
			"org.junit.jupiter.api.RepeatedTest", //
			"org.junit.jupiter.params.ParameterizedTest" ) );

	private static final Map<StackTraceElement, Integer> repeatableTestCaseAnnotationsCount = new HashMap<>();

	private TestCaseFinder() {}

	private static Function<TestCaseInformation, String> toClassName() {
		return info -> info.getStackTraceElement().getClassName();
	}

	private static Function<TestCaseInformation, String> toMethodName() {
		return info -> {
			final String methodName = info.getStackTraceElement().getMethodName();
			return info.isRepeatable() ? methodName + DELIMITER + info.getInvocationCount() : methodName;
		};
	}

	/**
	 * @return A <em>distinct</em> method name for the test case method in all stack traces.
	 */
	public static Optional<String> findTestCaseMethodNameInStack() {
		return findTestCaseMethodInStack( toMethodName() );
	}

	/**
	 * @return The class name for the test case method in all stack traces.
	 */
	public static Optional<String> findTestCaseClassNameInStack() {
		return findTestCaseMethodInStack( toClassName() );
	}

	/**
	 * @param trace
	 *            The trace to be used for search.
	 * @return A <em>distinct</em> method name for the test case method in the given stack trace.
	 */
	public static Optional<String> findTestCaseMethodNameInStack( final StackTraceElement[] trace ) {
		return findTestCaseMethodInStack( toMethodName(), trace );
	}

	/**
	 * @param trace
	 *            The trace to be used for search.
	 * @return The class name for the test case method in the given stack trace.
	 */
	public static Optional<String> findTestCaseClassNameInStack( final StackTraceElement[] trace ) {
		return findTestCaseMethodInStack( toClassName(), trace );
	}

	private static Optional<String> findTestCaseMethodInStack( final Function<TestCaseInformation, String> mapper ) {
		final TestCaseInformation info = findTestCaseMethodInStack();
		return info.isFound() ? Optional.of( mapper.apply( info ) ) : Optional.empty();
	}

	private static Optional<String> findTestCaseMethodInStack( final Function<TestCaseInformation, String> mapper,
			final StackTraceElement[] trace ) {
		final TestCaseInformation info = findTestCaseMethodInStack( trace );
		return info.isFound() ? Optional.of( mapper.apply( info ) ) : Optional.empty();
	}

	/**
	 * @return Test case information for the test case method in all stack traces.
	 */
	public static TestCaseInformation findTestCaseMethodInStack() {
		for ( final StackTraceElement[] stack : Thread.getAllStackTraces().values() ) {
			final TestCaseInformation info = findTestCaseMethodInStack( stack );
			if ( info.isFound() ) {
				return info;
			}
		}
		return NO_TEST_CASE_INFORMATION;
	}

	/**
	 * @param trace
	 *            The trace to be used for search.
	 * @return Test case information for the test case method in the given stack trace.
	 */
	public static TestCaseInformation findTestCaseMethodInStack( final StackTraceElement[] trace ) {
		for ( final StackTraceElement element : trace ) {
			final TestCaseAnnotationType type = determineTestCaseAnnotationType( element );
			if ( type == TestCaseAnnotationType.NORMAL ) {
				return new TestCaseInformation( element, type, 1 );
			}
			if ( type == TestCaseAnnotationType.REPEATABLE ) {
				final int count = repeatableTestCaseAnnotationsCount.merge( element, 1, Math::addExact );
				return new TestCaseInformation( element, type, count );
			}
		}
		return NO_TEST_CASE_INFORMATION;
	}

	private static TestCaseAnnotationType determineTestCaseAnnotationType( final StackTraceElement element ) {
		final Method method = tryToFindMethodForStackTraceElement( element );
		if ( method == null ) {
			return TestCaseAnnotationType.NONE;
		}

		final Annotation[] annotations = method.getAnnotations();
		for ( final Annotation annotation : annotations ) {
			final String annotationName = annotation.annotationType().getName();
			if ( testCaseAnnotations.contains( annotationName ) ) {
				return TestCaseAnnotationType.NORMAL;
			}
			if ( repeatableTestCaseAnnotations.contains( annotationName ) ) {
				return TestCaseAnnotationType.REPEATABLE;
			}
		}

		return TestCaseAnnotationType.NONE;
	}

	private static Method tryToFindMethodForStackTraceElement( final StackTraceElement element ) {
		final Class<?> clazz;
		Method method = null;

		try {
			clazz = Class.forName( element.getClassName() );
		} catch ( final ClassNotFoundException e ) {
			return null;
		}
		try {
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
		} catch ( final NoClassDefFoundError e ) {
			log.error( "Could not analyze method due to NoClassDefFoundError.", e );
		}
		return method;
	}

	@Value
	public static class TestCaseInformation {
		StackTraceElement stackTraceElement;
		TestCaseAnnotationType testCaseAnnotationType;
		int invocationCount;

		public boolean isFound() {
			return stackTraceElement != null;
		}

		public boolean isRepeatable() {
			return testCaseAnnotationType == TestCaseAnnotationType.REPEATABLE;
		}
	}

	public static enum TestCaseAnnotationType {
		NORMAL,
		REPEATABLE,
		NONE
	}

}
