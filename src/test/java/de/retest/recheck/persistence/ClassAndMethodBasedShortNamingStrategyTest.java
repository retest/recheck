package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClassAndMethodBasedShortNamingStrategyTest {

	static class InnerClass {
		@Test
		void getSuiteName_should_return_test_class_simple_name() throws Exception {
			final NamingStrategy cut = new ClassAndMethodBasedShortNamingStrategy();
			assertThat( cut.getSuiteName() )
					.isEqualTo( ClassAndMethodBasedShortNamingStrategyTest.class.getSimpleName() );
		}
	}

	@Test
	void getSuiteName_should_return_test_class_simple_name() throws Exception {
		final NamingStrategy cut = new ClassAndMethodBasedShortNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getSimpleName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final NamingStrategy cut = new ClassAndMethodBasedShortNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
