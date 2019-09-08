package de.retest.recheck;

import static de.retest.recheck.Properties.GOLDEN_MASTER_FILE_EXTENSION;
import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.FileNamer;

class LegacyCompatibilityFileNamerStrategyTest {

	class FileNamerStrategyImpl extends MavenConformFileNamerStrategy {
	}

	@Test
	void test_class_name_should_keep_working() throws Exception {
		final FileNamerStrategy cut = new FileNamerStrategyImpl();
		assertThat( cut.getTestClassName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void test_method_name_should_keep_working() throws Exception {
		final FileNamerStrategy cut = new FileNamerStrategyImpl();
		assertThat( cut.getTestMethodName() ).isEqualTo( "test_method_name_should_keep_working" );
	}

	@Test
	void getFile_should_keep_working() throws Exception {
		final FileNamer cut = new FileNamerStrategyImpl().createFileNamer( "suitename", "testname", "check" );
		assertThat( cut.getFile( GOLDEN_MASTER_FILE_EXTENSION ).getPath() )
				.isEqualTo( "src/test/resources/retest/recheck/suitename/testname/check.recheck" );
	}

	@Test
	void getResultFile_should_keep_working() throws Exception {
		final FileNamer cut = new FileNamerStrategyImpl().createFileNamer( "suitename", "testname", "check" );
		assertThat( cut.getResultFile( TEST_REPORT_FILE_EXTENSION ).getPath() )
				.isEqualTo( "target/test-classes/retest/recheck/suitename/testname/check.report" );
	}

}
