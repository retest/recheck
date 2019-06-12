package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCaseFinderJunitVintageTest {

	@Test
	public void junit_vintage_test_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_vintage_test_annotation_should_be_found" );
	}

	@Before
	public void junit_vintage_before_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_vintage_before_annotation_should_be_found" );
	}

	@After
	public void junit_vintage_after_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_vintage_after_annotation_should_be_found" );
	}

	@BeforeClass
	public static void junit_vintage_before_class_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( TestCaseFinderJunitVintageTest.class.getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_vintage_before_class_annotation_should_be_found" );
	}

	@AfterClass
	public static void junit_vintage_after_class_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( TestCaseFinderJunitVintageTest.class.getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_vintage_after_class_annotation_should_be_found" );
	}

}
