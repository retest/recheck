package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.FileNamer;

class FileNamerStrategyTest {

	class FileNamerStrategyImpl implements FileNamerStrategy {
		@Override
		public FileNamer createFileNamer() {
			return null;
		}
	}

	@Test
	void default_test_class_name_should_be_implemented() throws Exception {
		final FileNamerStrategy cut = new FileNamerStrategyImpl();
		assertThat( cut.getTestClassName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void default_test_method_name_should_be_implemented() throws Exception {
		final FileNamerStrategy cut = new FileNamerStrategyImpl();
		assertThat( cut.getTestMethodName() ).isEqualTo( "default_test_method_name_should_be_implemented" );
	}

}
