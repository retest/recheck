package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
class TestCaseFinderTest {

	@Nested
	class SomeTest {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_test() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class SomeIT {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_it() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class SomeTestCase {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_testcase() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class SomeITCase {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_itcase() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class SomeTests {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_tests() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class TestSome {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_prefix_test() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

	@Nested
	class ITSome {

		final Optional<String> className = TestCaseFinder.getInstance().findTestCaseClassNameInStack();

		@Test
		void test_case_finder_should_print_class_name_prefix_it() throws Exception {
			assertThat( className ).hasValue( getClass().getName() );
		}
	}

}
