package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class GradleProjectLayoutTest {

	@Test
	void files_should_be_gradle_conform() throws Exception {
		final GradleProjectLayout layout = new GradleProjectLayout();
		final Path goldenMaster = layout.getGoldenMaster( "foo", "", "bar" );
		final Path resultFile = layout.getReport( "bar" );

		assertThat( goldenMaster.toString() ).isEqualTo( "src/test/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.toString() ).isEqualTo( "build/test-results/test/retest/recheck/bar.report" );
	}

	@Test
	void files_should_be_in_the_correct_source_set() {
		final GradleProjectLayout layout = new GradleProjectLayout( "integrationTest" );
		final Path goldenMaster = layout.getGoldenMaster( "foo", "", "bar" );
		final Path resultFile = layout.getReport( "bar" );

		assertThat( goldenMaster.toString() )
				.isEqualTo( "src/integrationTest/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.toString() ).isEqualTo( "build/test-results/integrationTest/retest/recheck/bar.report" );
	}

	@Test
	void should_not_allow_null_source_set_name() {
		assertThatThrownBy( () -> new GradleProjectLayout( null ) ).isExactlyInstanceOf( NullPointerException.class )
				.hasMessage( "sourceSetName cannot be null!" );
	}

	@Test
	void should_not_allow_empty_source_set_name() {
		assertThatThrownBy( () -> new GradleProjectLayout( "" ) ).isExactlyInstanceOf( IllegalArgumentException.class )
				.hasMessage( "sourceSetName cannot be empty!" );
	}

	@Test
	void getTestResourceRoot_for_test_should_return_correct_path() throws Exception {
		final GradleProjectLayout cut = new GradleProjectLayout( "test" );

		assertThat( cut.getTestSourcesRoot() ).hasValue( Paths.get( "src/test/java" ) );
	}

	@Test
	void getTestResourceRoot_for_main_should_still_return_correct_path() throws Exception {
		final GradleProjectLayout cut = new GradleProjectLayout( "main" );

		assertThat( cut.getTestSourcesRoot() ).hasValue( Paths.get( "src/test/java" ) );
	}
}
