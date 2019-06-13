package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

class GoldenMasterProviderImplTest {

	private static final String NON_EXISTING_FILE = "blubber_schmu";
	private static final String EXISTING_FILE = "existing";

	GoldenMasterProviderImpl cut;

	@BeforeEach
	void setUp( @TempDir final Path temp ) {
		cut = new GoldenMasterProviderImpl( null );
	}

	@Test
	void null_should_result_in_error() {
		assertThatThrownBy( () -> cut.getGoldenMaster( null ) ).isInstanceOf( NoGoldenMasterFoundException.class );
	}

	@Test
	void non_existing_file_should_throw_error( @TempDir final Path temp ) {
		final File nonExistingFile = temp.resolve( NON_EXISTING_FILE ).toFile();

		assertThatThrownBy( () -> cut.getGoldenMaster( nonExistingFile.getPath() ) )
				.isInstanceOf( NoGoldenMasterFoundException.class );
	}

	@Test
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void existing_file_outside_project_root_should_throw_error( @TempDir final Path tempFolder ) throws IOException {
		final File existingFile = tempFolder.resolve( EXISTING_FILE ).toFile();
		existingFile.createNewFile();

		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, tempFolder.toString() );

		assertThatThrownBy( () -> cut.getGoldenMaster( existingFile.getPath() ) )
				.isInstanceOf( NoGoldenMasterFoundException.class );
	}

	@Test
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void existing_file_in_project_root_should_be_ok( @TempDir final Path tempFolder ) throws Exception {
		final File existingFile = tempFolder.resolve( EXISTING_FILE ).toFile();
		existingFile.createNewFile();
		Files.createDirectories( tempFolder.resolve( "src/main/java" ) );
		Files.createDirectories( tempFolder.resolve( "src/test/java" ) );
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, tempFolder.toString() );

		final File gm = cut.getGoldenMaster( EXISTING_FILE );

		assertThat( gm ).isEqualTo( existingFile );
	}

}
