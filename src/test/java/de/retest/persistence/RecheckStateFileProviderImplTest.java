package de.retest.persistence;

import static de.retest.recheck.persistence.RecheckStateFileProviderImpl.RECHECK_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import de.retest.recheck.persistence.NoStateFileFoundException;
import de.retest.recheck.persistence.RecheckStateFileProviderImpl;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

@ExtendWith( TempDirectory.class )
class RecheckStateFileProviderImplTest {

	private static final String NON_EXISTING_FILE = "blubber_schmu";
	private static final String EXISTING_FILE = "existing";

	RecheckStateFileProviderImpl cut;

	@BeforeEach
	void setUp( @TempDir final Path temp ) {
		cut = new RecheckStateFileProviderImpl( null );
	}

	@Test
	void null_should_result_in_error() {
		assertThatThrownBy( () -> cut.getRecheckStateFile( null ) ).isInstanceOf( NoStateFileFoundException.class );
	}

	@Test
	void non_existing_file_should_throw_error( @TempDir final Path temp ) {
		final File nonExistingFile = temp.resolve( NON_EXISTING_FILE ).toFile();

		assertThatThrownBy( () -> cut.getRecheckStateFile( nonExistingFile.getAbsolutePath() ) )
				.isInstanceOf( NoStateFileFoundException.class );
	}

	@Test
	@SystemProperty( key = RECHECK_PROJECT_ROOT )
	void existing_file_outside_project_root_should_throw_error( @TempDir final Path tempFolder ) throws IOException {
		final File existingFile = tempFolder.resolve( EXISTING_FILE ).toFile();
		existingFile.createNewFile();

		System.setProperty( RECHECK_PROJECT_ROOT, tempFolder.toString() );

		assertThatThrownBy( () -> cut.getRecheckStateFile( existingFile.getPath() ) )
				.isInstanceOf( NoStateFileFoundException.class );
	}

	@Test
	@SystemProperty( key = RECHECK_PROJECT_ROOT )
	void existing_file_in_project_root_should_be_ok( @TempDir final Path tempFolder ) throws Exception {
		final File existingFile = tempFolder.resolve( EXISTING_FILE ).toFile();
		existingFile.createNewFile();

		System.setProperty( RECHECK_PROJECT_ROOT, tempFolder.toString() );

		final File f = cut.getRecheckStateFile( EXISTING_FILE );

		assertThat( f ).isEqualTo( existingFile );
	}

}
