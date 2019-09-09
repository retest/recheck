package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.JUnitTestbasedShortNamingStrategy;

class JUnitTestbasedShortFileNamerStrategyTest {

	static class InnerClass {
		@Test
		void getSuiteName_should_return_test_class_simple_name() throws Exception {
			final JUnitTestbasedShortNamingStrategy cut = new JUnitTestbasedShortNamingStrategy();
			assertThat( cut.getSuiteName() )
					.isEqualTo( JUnitTestbasedShortFileNamerStrategyTest.class.getSimpleName() );
		}
	}

	@Test
	void getSuiteName_should_return_test_class_simple_name() throws Exception {
		final JUnitTestbasedShortNamingStrategy cut = new JUnitTestbasedShortNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getSimpleName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final JUnitTestbasedShortNamingStrategy cut = new JUnitTestbasedShortNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
