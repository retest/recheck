package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.retest.recheck.TestCaseFinder.TestCaseAnnotationType;
import de.retest.recheck.TestCaseFinder.TestCaseInformation;

class TestCaseFinderJunitJupiterTest {

	@Test
	void empty_trace_should_yield_nothing() throws Exception {
		final StackTraceElement[] emptyTrace = new StackTraceElement[0];

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack( emptyTrace );
		assertThat( className ).isEmpty();

		final Optional<String> methodName = TestCaseFinder.getInstance().findTestCaseMethodNameInStack( emptyTrace );
		assertThat( methodName ).isEmpty();

		final TestCaseInformation info = TestCaseFinder.getInstance().findTestCaseMethodInStack( emptyTrace );
		assertThat( info ).isSameAs( TestCaseFinder.NO_TEST_CASE_INFORMATION );
	}

	@Test
	void junit_jupiter_test_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_jupiter_test_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@BeforeEach
	void junit_jupiter_before_each_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_jupiter_before_each_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@AfterEach
	void junit_jupiter_after_each_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_jupiter_after_each_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@BeforeAll
	static void junit_jupiter_before_all_annotation_should_be_found() throws Exception {
		final String expectedClassName = TestCaseFinderJunitJupiterTest.class.getName();
		final String expectedMethodName = "junit_jupiter_before_all_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@AfterAll
	static void junit_jupiter_after_all_annotation_should_be_found() throws Exception {
		final String expectedClassName = TestCaseFinderJunitJupiterTest.class.getName();
		final String expectedMethodName = "junit_jupiter_after_all_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@ParameterizedTest
	@ValueSource( ints = { 1, 2 } )
	void junit_jupiter_parameterized_test_annotation_should_be_found( final int count ) throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_jupiter_parameterized_test_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.REPEATABLE;
		final int expectedInvocationCount = count;

		final Optional<String> actualClassName = TestCaseFinder.getInstance().findTestCaseClassNameInStack();
		assertThat( actualClassName ).hasValue( expectedClassName );

		final Optional<String> actualMethodName = TestCaseFinder.getInstance().findTestCaseMethodNameInStack();
		assertThat( actualMethodName ).hasValueSatisfying( methodName -> methodName.startsWith( expectedMethodName ) );

		final TestCaseInformation info = TestCaseFinder.getInstance().findTestCaseMethodInStack();
		assertThat( info.getStackTraceElement().getClassName() ).isEqualTo( expectedClassName );
		assertThat( info.getStackTraceElement().getMethodName() ).isEqualTo( expectedMethodName );
		assertThat( info.getTestCaseAnnotationType() ).isEqualTo( expectedType );
		assertThat( info.getInvocationCount() ).isEqualTo( expectedInvocationCount );

	}

	@RepeatedTest( value = 2 )
	void junit_jupiter_repeated_test_should_be_found( final RepetitionInfo repetitionInfo ) {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_jupiter_repeated_test_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.REPEATABLE;
		final int expectedInvocationCount = repetitionInfo.getCurrentRepetition();

		final Optional<String> actualClassName = TestCaseFinder.getInstance().findTestCaseClassNameInStack();
		assertThat( actualClassName ).hasValue( expectedClassName );

		final Optional<String> actualMethodName = TestCaseFinder.getInstance().findTestCaseMethodNameInStack();
		assertThat( actualMethodName ).hasValueSatisfying( methodName -> methodName.startsWith( expectedMethodName ) );

		final TestCaseInformation info = TestCaseFinder.getInstance().findTestCaseMethodInStack();
		assertThat( info.getStackTraceElement().getClassName() ).isEqualTo( expectedClassName );
		assertThat( info.getStackTraceElement().getMethodName() ).isEqualTo( expectedMethodName );
		assertThat( info.getTestCaseAnnotationType() ).isEqualTo( expectedType );
		assertThat( info.getInvocationCount() ).isEqualTo( expectedInvocationCount );
	}

	private static void assertAll( final String expectedClassName, final String expectedMethodName,
			final TestCaseAnnotationType expectedType, final int expectedInvocationCount ) {
		final Optional<String> actualClassName = TestCaseFinder.getInstance().findTestCaseClassNameInStack();
		assertThat( actualClassName ).hasValue( expectedClassName );

		final Optional<String> actualMethodName = TestCaseFinder.getInstance().findTestCaseMethodNameInStack();
		assertThat( actualMethodName ).hasValue( expectedMethodName );

		final TestCaseInformation info = TestCaseFinder.getInstance().findTestCaseMethodInStack();
		assertThat( info.getStackTraceElement().getClassName() ).isEqualTo( expectedClassName );
		assertThat( info.getStackTraceElement().getMethodName() ).isEqualTo( expectedMethodName );
		assertThat( info.getTestCaseAnnotationType() ).isEqualTo( expectedType );
		assertThat( info.getInvocationCount() ).isEqualTo( expectedInvocationCount );
	}

}
