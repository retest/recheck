package de.retest.recheck;

import static de.retest.recheck.persistence.ProjectLayouts.BUILD_GRADLE;
import static de.retest.recheck.persistence.ProjectLayouts.POM_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.ClearSystemProperty;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.persistence.GradleProjectLayout;
import de.retest.recheck.persistence.MavenProjectLayout;
import de.retest.recheck.persistence.ProjectLayout;

class RecheckOptionsIT {

	@Nested
	@ClearSystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	class RecheckOptionsBuilderIT {

		Path root;

		@BeforeEach
		void setUp( @TempDir final Path root ) throws IOException {
			this.root = root;
			System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, root.toString() );
			createLayout( root );
		}

		@Test
		void layout_should_not_throw_if_no_project_only_maven_is_present() throws IOException {
			createFile( root, POM_XML );

			final RecheckOptions options = RecheckOptions.builder().build();

			assertThat( options.getProjectLayout() ).isInstanceOf( MavenProjectLayout.class );
		}

		@Test
		void layout_should_identify_if_project_and_maven_present() throws IOException {
			createFolder( root );
			createFile( root, POM_XML );

			final RecheckOptions options = RecheckOptions.builder().build();

			assertThat( options.getProjectLayout() ).isInstanceOf( MavenProjectLayout.class );
		}

		@Test
		void layout_should_not_throw_if_no_project_only_gradle_is_present() throws IOException {
			createFile( root, BUILD_GRADLE );

			final RecheckOptions options = RecheckOptions.builder().build();

			assertThat( options.getProjectLayout() ).isInstanceOf( GradleProjectLayout.class );
		}

		@Test
		void layout_should_identify_if_project_and_gradle_present() throws IOException {
			createFolder( root );
			createFile( root, BUILD_GRADLE );

			final RecheckOptions options = RecheckOptions.builder().build();

			assertThat( options.getProjectLayout() ).isInstanceOf( GradleProjectLayout.class );
		}

		@Test
		void layout_should_prefer_maven_over_gradle() throws IOException {
			createFolder( root );
			createFile( root, POM_XML );
			createFile( root, BUILD_GRADLE );

			final RecheckOptions options = RecheckOptions.builder().build();

			assertThat( options.getProjectLayout() ).isInstanceOf( MavenProjectLayout.class );
		}

		@ParameterizedTest
		@ValueSource( strings = { POM_XML, BUILD_GRADLE } )
		void layout_should_use_specified_layout( final String file ) throws IOException {
			createFolder( root );
			createFile( root, file );

			final ProjectLayout layout = mock( ProjectLayout.class );

			final RecheckOptions options = RecheckOptions.builder() //
					.projectLayout( layout ) //
					.build();

			assertThat( options.getProjectLayout() ).isEqualTo( layout );
		}
	}

	private void createLayout( final Path root ) throws IOException {
		Files.createDirectories( root.resolve( "src/main/java" ) );
		Files.createDirectories( root.resolve( "src/test/java" ) );
	}

	private void createFolder( final Path root ) throws IOException {
		Files.createDirectory( root.resolve( RecheckProperties.RETEST_FOLDER_NAME ) );
	}

	private void createFile( final Path root, final String file ) throws IOException {
		Files.createFile( root.resolve( file ) );
	}
}
