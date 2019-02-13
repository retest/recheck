package de.retest.recheck.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class ProjectRootFinderTest {

	@Test
	void project_root_should_be_found() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path actual = ProjectRootFinderUtil.getProjectRoot();

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_be_found_from_subpath() throws Exception {
		final Path expected = Paths.get( "" ).toAbsolutePath();
		final Path pathInTarget = Paths.get( "target/test-classes" ).toAbsolutePath();

		final Path actual = ProjectRootFinderUtil.getProjectRoot( pathInTarget );

		assertThat( expected ).isEqualTo( actual );
	}

	@Test
	void project_root_should_throw_error_on_root_base() throws Exception {
		assertThatThrownBy( () -> ProjectRootFinderUtil.getProjectRoot( Paths.get( "/" ) ) )
				.isInstanceOf( UncheckedIOException.class );
	}

	@Test
	void project_root_should_throw_error_on_non_existing_base() throws Exception {
		assertThatThrownBy( () -> ProjectRootFinderUtil.getProjectRoot( Paths.get( "/bla/blub/42" ) ) )
				.isInstanceOf( UncheckedIOException.class );
	}

	@Test
	void project_root_should_throw_error_on_null_base() throws Exception {
		assertThatThrownBy( () -> ProjectRootFinderUtil.getProjectRoot( null ) )
				.isInstanceOf( UncheckedIOException.class );
	}

}
