package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.retest.recheck.TestCaseFinder.TestCaseAnnotationType;
import de.retest.recheck.TestCaseFinder.TestCaseInformation;

public class TestCaseFinderJunitVintageTest {

	@Test
	public void junit_vintage_test_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_vintage_test_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@Before
	public void junit_vintage_before_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_vintage_before_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@After
	public void junit_vintage_after_annotation_should_be_found() throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_vintage_after_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@BeforeClass
	public static void junit_vintage_before_class_annotation_should_be_found() throws Exception {
		final String expectedClassName = TestCaseFinderJunitVintageTest.class.getName();
		final String expectedMethodName = "junit_vintage_before_class_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
	}

	@AfterClass
	public static void junit_vintage_after_class_annotation_should_be_found() throws Exception {
		final String expectedClassName = TestCaseFinderJunitVintageTest.class.getName();
		final String expectedMethodName = "junit_vintage_after_class_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.NORMAL;
		final int expectedInvocationCount = 1;
		assertAll( expectedClassName, expectedMethodName, expectedType, expectedInvocationCount );
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
