package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.ClassAndMethodBasedNamingStrategy;

class ClassAndMethodBasedNamingStrategyTest {

	@Test
	void getSuiteName_should_return_qualified_test_class_name() throws Exception {
		final ClassAndMethodBasedNamingStrategy cut = new ClassAndMethodBasedNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final ClassAndMethodBasedNamingStrategy cut = new ClassAndMethodBasedNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
