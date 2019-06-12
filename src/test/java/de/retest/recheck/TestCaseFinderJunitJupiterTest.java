package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestCaseFinderJunitJupiterTest {

	@Test
	void junit_jupiter_test_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_test_annotation_should_be_found" );
	}

	@BeforeEach
	void junit_jupiter_before_each_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_before_each_annotation_should_be_found" );
	}

	@AfterEach
	void junit_jupiter_after_each_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( getClass().getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_after_each_annotation_should_be_found" );
	}

	@BeforeAll
	static void junit_jupiter_before_all_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( TestCaseFinderJunitJupiterTest.class.getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_before_all_annotation_should_be_found" );
	}

	@AfterAll
	static void junit_jupiter_after_all_annotation_should_be_found() throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( TestCaseFinderJunitJupiterTest.class.getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_after_all_annotation_should_be_found" );
	}

	@ParameterizedTest
	@ValueSource( strings = { "foo", "bar" } )
	void junit_jupiter_parameterized_test_annotation_should_be_found( final String s ) throws Exception {
		final Optional<String> className = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( className ).hasValue( TestCaseFinderJunitJupiterTest.class.getName() );
		final Optional<String> methodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( methodName ).hasValue( "junit_jupiter_parameterized_test_annotation_should_be_found" );
	}

}
