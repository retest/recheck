package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DeleteOnCloseFileInputStreamTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void file_should_be_deleted_on_close() throws IOException {
		final File file = tempFolder.newFile();
		final InputStream in = new DeleteOnCloseFileInputStream( file );

		in.close();

		assertThat( file ).doesNotExist();
	}

}
