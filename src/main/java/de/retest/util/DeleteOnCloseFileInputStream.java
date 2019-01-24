package de.retest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * Taken from https://stackoverflow.com/a/4694155
 */
public class DeleteOnCloseFileInputStream extends FileInputStream {

	private File file;

	public DeleteOnCloseFileInputStream( final File file ) throws FileNotFoundException {
		super( file );
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			if ( file != null ) {
				file.delete();
				file = null;
			}
		}
	}

}
