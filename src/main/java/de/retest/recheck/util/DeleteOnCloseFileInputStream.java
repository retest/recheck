package de.retest.recheck.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

/*
 * Taken from https://stackoverflow.com/a/4694155
 */
@Slf4j
public class DeleteOnCloseFileInputStream extends FileInputStream {

	private final File file;

	public DeleteOnCloseFileInputStream( final File file ) throws FileNotFoundException {
		super( file );
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			if ( file.exists() ) {
				FileUtils.forceDelete( file );
			} else {
				log.debug( "File '{}' has already been deleted.", file );
			}
		}
	}
}
