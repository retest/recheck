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

	public static Optional<String> findTestCaseMethodNameInStack() {
		return findTestCaseMethodInStack( toMethodName() );
	}

	public static Optional<String> findTestCaseClassNameInStack() {
		return findTestCaseMethodInStack( toClassName() );
	}

	public static Optional<String> findTestCaseMethodNameInStack( final StackTraceElement[] trace ) {
		return findTestCaseMethodInStack( toMethodName(), trace );
	}

	public static Optional<String> findTestCaseClassNameInStack( final StackTraceElement[] trace ) {
		return findTestCaseMethodInStack( toClassName(), trace );
	}

	private static Optional<String> findTestCaseMethodInStack( final Function<TestCaseInformation, String> mapper ) {
		return Optional.ofNullable( findTestCaseMethodInStack() ).map( mapper );
	}

	private static Optional<String> findTestCaseMethodInStack( final Function<TestCaseInformation, String> mapper,
			final StackTraceElement[] trace ) {
		return Optional.ofNullable( findTestCaseMethodInStack( trace ) ).map( mapper );
	}

	public static TestCaseInformation findTestCaseMethodInStack() {
		for ( final StackTraceElement[] stack : Thread.getAllStackTraces().values() ) {
			final TestCaseInformation info = findTestCaseMethodInStack( stack );
			if ( info != null ) {
				return info;
			}
		}
		return null;
	}

	public static TestCaseInformation findTestCaseMethodInStack( final StackTraceElement[] trace ) {
		for ( final StackTraceElement element : trace ) {
			final TestCaseAnnotationType type = determineTestCaseAnnotation( element );
			if ( type == TestCaseAnnotationType.NORMAL ) {
				return new TestCaseInformation( element, type, 1 );
			}
			if ( type == TestCaseAnnotationType.REPEATABLE ) {
				final int count = repeatableTestCaseAnnotationsCount.merge( element, 1, Math::addExact );
				return new TestCaseInformation( element, type, count );
			}
		}
		return null;
	}

	private static TestCaseAnnotationType determineTestCaseAnnotation( final StackTraceElement element ) {
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
