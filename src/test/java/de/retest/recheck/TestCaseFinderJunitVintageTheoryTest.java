package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import de.retest.recheck.TestCaseFinder.TestCaseAnnotationType;
import de.retest.recheck.TestCaseFinder.TestCaseInformation;

@RunWith( Theories.class )
public class TestCaseFinderJunitVintageTheoryTest {

	@DataPoints
	public static final int[] data = { 1, 2 };

	@Theory
	public void junit_vintage_theory_annotation_should_be_found( final int count ) throws Exception {
		final String expectedClassName = getClass().getName();
		final String expectedMethodName = "junit_vintage_theory_annotation_should_be_found";
		final TestCaseAnnotationType expectedType = TestCaseAnnotationType.REPEATABLE;
		final int expectedInvocationCount = count;

		final Optional<String> actualClassName = TestCaseFinder.findTestCaseClassNameInStack();
		assertThat( actualClassName ).hasValue( expectedClassName );

		final Optional<String> actualMethodName = TestCaseFinder.findTestCaseMethodNameInStack();
		assertThat( actualMethodName ).hasValueSatisfying( methodName -> methodName.startsWith( expectedMethodName ) );

		final TestCaseInformation info = TestCaseFinder.findTestCaseMethodInStack();
		assertThat( info.getStackTraceElement().getClassName() ).isEqualTo( expectedClassName );
		assertThat( info.getStackTraceElement().getMethodName() ).isEqualTo( expectedMethodName );
		assertThat( info.getTestCaseAnnotationType() ).isEqualTo( expectedType );
		assertThat( info.getInvocationCount() ).isEqualTo( expectedInvocationCount );
	}

}
