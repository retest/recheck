package de.retest.recheck.persistence;

import static de.retest.recheck.persistence.ProjectLayouts.BUILD_GRADLE;
import static de.retest.recheck.persistence.ProjectLayouts.POM_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.ClearSystemProperty;

import de.retest.recheck.RecheckProperties;
import de.retest.recheck.configuration.ProjectConfiguration;

@ClearSystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
class ProjectLayoutsIT {

	@BeforeEach
	void setUp( @TempDir final Path root ) {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, root.toString() );
	}

	@Test
	void detect_should_throw_if_project_not_present() {
		assertThatThrownBy( ProjectLayouts::detect ) //
				.isInstanceOf( IllegalStateException.class ) //
				.hasMessageContaining( "Could not detect project layout" );
	}

	@ParameterizedTest
	@ValueSource( strings = { POM_XML, BUILD_GRADLE } )
	void detect_should_throw_if_project_does_not_exist( final String file, @TempDir final Path root ) throws IOException {
		createFile( root, file );

		assertThatThrownBy( ProjectLayouts::detect ) //
				.isInstanceOf( IllegalStateException.class ) //
				.hasMessageContaining( "Could not detect project layout" );
	}

	@Test
	void detect_should_throw_if_project_present_without_indicator_file( @TempDir final Path root ) throws IOException {
		createFolder( root );

		assertThatThrownBy( ProjectLayouts::detect ) //
				.isInstanceOf( IllegalStateException.class ) //
				.hasMessageContaining( "Could not detect project layout" );
	}

	@Test
	void detect_should_be_maven_if_pom_present( @TempDir final Path root ) throws IOException {
		createFolder( root );
		createFile( root, POM_XML );

		assertThat( ProjectLayouts.detect() ).isInstanceOf( MavenProjectLayout.class );
	}

	@Test
	void detect_should_be_gradle_if_build_present( @TempDir final Path root ) throws IOException {
		createFolder( root );
		createFile( root, BUILD_GRADLE );

		assertThat( ProjectLayouts.detect() ).isInstanceOf( GradleProjectLayout.class );
	}

	@Test
	void detect_should_prefer_maven_over_gradle( @TempDir final Path root ) throws IOException {
		createFolder( root );
		createFile( root, POM_XML );
		createFile( root, BUILD_GRADLE );

		assertThat( ProjectLayouts.detect() ).isInstanceOf( MavenProjectLayout.class );
	}

	private void createFolder( final Path root ) throws IOException {
		Files.createDirectory( root.resolve( RecheckProperties.RETEST_FOLDER_NAME ) );
	}

	private void createFile( final Path root, final String file ) throws IOException {
		Files.createFile( root.resolve( file ) );
	}
}
