package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.JUnitTestbasedNamingStrategy;

class JUnitTestbasedFileNamerStrategyTest {

	@Test
	void getSuiteName_should_return_qualified_test_class_name() throws Exception {
		final JUnitTestbasedNamingStrategy cut = new JUnitTestbasedNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final JUnitTestbasedNamingStrategy cut = new JUnitTestbasedNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
