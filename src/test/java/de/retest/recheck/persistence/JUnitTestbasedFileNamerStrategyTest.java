package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.JunitbasedNamingStrategy;

class JUnitTestbasedFileNamerStrategyTest {

	@Test
	void getSuiteName_should_return_qualified_test_class_name() throws Exception {
		final JunitbasedNamingStrategy cut = new JunitbasedNamingStrategy();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void getTestName_should_return_test_method_name() throws Exception {
		final JunitbasedNamingStrategy cut = new JunitbasedNamingStrategy();
		assertThat( cut.getTestName() ).isEqualTo( "getTestName_should_return_test_method_name" );
	}

}
