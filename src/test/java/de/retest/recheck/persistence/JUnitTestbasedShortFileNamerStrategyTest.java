package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.JunitbasedShortNamingStrategy;

class JUnitTestbasedShortFileNamerStrategyTest {

	static class InnerClass {
		@Test
		void getSuiteName_should_return_test_class_simple_name() throws Exception {
			final JunitbasedShortNamingStrategy cut = new JunitbasedShortNamingStrategy();
			assertThat( cut.getSuiteName() )
					.isEqualTo( JUnitTestbasedShortFileNamerStrategyTest.class.getSimpleName() );
		}
	}

	@Test
	void getSuiteName_should_return_test_class_simple_name() throws Exception {
		final JunitbasedShortNamingStrategy cut = new JunitbasedShortNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getSimpleName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final JunitbasedShortNamingStrategy cut = new JunitbasedShortNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
