package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DeleteOnCloseFileInputStreamTest {

	@Test
	void file_should_be_deleted_on_close( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "foo" );
		Files.createFile( file );
		final InputStream in = new DeleteOnCloseFileInputStream( file.toFile() );

		in.close();

		assertThat( file ).doesNotExist();
	}

	@Test
	void already_deleted_file_should_cause_no_exception( @TempDir final Path temp ) throws IOException {
		final Path file = temp.resolve( "bar" );
		Files.createFile( file );
		final InputStream in = new DeleteOnCloseFileInputStream( file.toFile() );

		Files.delete( file );
		in.close();

		assertThat( file ).doesNotExist();
	}

}
