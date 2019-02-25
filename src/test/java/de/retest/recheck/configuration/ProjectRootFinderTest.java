package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class ProjectRootFinderTest {

	@Test
	void project_root_should_be_found_from_absolute_path() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path actual = ProjectRootFinderUtil.getProjectRoot( Paths.get( "" ).toAbsolutePath() ).get();

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_be_found_from_absolute_subpath() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path pathInTarget = Paths.get( "target/test-classes" ).toAbsolutePath();

		final Path actual = ProjectRootFinderUtil.getProjectRoot( pathInTarget ).get();

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_be_found_from_relative_path() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path actual = ProjectRootFinderUtil.getProjectRoot().get();

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_be_found_from_relative_subpath() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path pathInTarget = Paths.get( "target/test-classes" );

		final Path actual = ProjectRootFinderUtil.getProjectRoot( pathInTarget ).get();

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_be_empty_on_root_base() throws Exception {
		assertThat( ProjectRootFinderUtil.getProjectRoot( Paths.get( "/" ) ) ).isNotPresent();
	}

	@Test
	void project_root_should_be_empty_on_non_existing_base() throws Exception {
		assertThat( ProjectRootFinderUtil.getProjectRoot( Paths.get( "/bla/blub/42" ) ) ).isNotPresent();
	}

	@Test
	void project_root_should_be_empty_on_null_base() throws Exception {
		assertThat( ProjectRootFinderUtil.getProjectRoot( null ) ).isNotPresent();
	}

}
