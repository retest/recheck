package de.retest.recheck.testcase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestCaseFinderTest {

	@Test
	public void junit_vintage_test_cases_should_be_found() throws Exception {
		final StackTraceElement element = TestCaseFinder.findTestCaseMethodInStack();
		assertThat( element.getClassName() ).isEqualTo( getClass().getName() );
		assertThat( element.getMethodName() ).isEqualTo( "junit_vintage_test_cases_should_be_found" );
	}

}
