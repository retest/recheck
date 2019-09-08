package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.FileNamer;

class GradleConformFileNamerStrategyTest {

	@Test
	void files_should_be_gradle_conform() throws Exception {
		final FileNamerStrategy cut = new GradleConformFileNamerStrategy();

		final FileNamer fileNamer = cut.getFileNamer();
		final File goldenMaster = fileNamer.getGoldenMaster( "foo", "", "bar" );
		final File resultFile = fileNamer.getReport( "bar" );

		assertThat( goldenMaster.getPath() ).isEqualTo( "src/test/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.getPath() ).isEqualTo( "build/test-results/test/retest/recheck/bar.report" );
	}

	@Test
	void files_should_be_in_the_correct_source_set() {
		final FileNamerStrategy cut = new GradleConformFileNamerStrategy( "integrationTest" );

		final FileNamer fileNamer = cut.getFileNamer();
		final File goldenMaster = fileNamer.getGoldenMaster( "foo", "", "bar" );
		final File resultFile = fileNamer.getReport( "bar" );

		assertThat( goldenMaster.getPath() )
				.isEqualTo( "src/integrationTest/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.getPath() )
				.isEqualTo( "build/test-results/integrationTest/retest/recheck/bar.report" );
	}

	@Test
	void should_not_allow_null_source_set_name() {
		assertThatThrownBy( () -> new GradleConformFileNamerStrategy( null ) )
				.isExactlyInstanceOf( NullPointerException.class ).hasMessage( "sourceSetName cannot be null!" );
	}

	@Test
	void should_not_allow_empty_source_set_name() {
		assertThatThrownBy( () -> new GradleConformFileNamerStrategy( "" ) )
				.isExactlyInstanceOf( IllegalArgumentException.class ).hasMessage( "sourceSetName cannot be empty!" );
	}
}
