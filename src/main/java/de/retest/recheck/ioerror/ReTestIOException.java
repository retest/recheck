package de.retest.recheck.ioerror;

import java.io.File;
import java.io.IOException;

import de.retest.recheck.util.FileUtil;

public class ReTestIOException extends IOException {

	private static final long serialVersionUID = 1L;
	private final File file;

	public ReTestIOException( final File file, final Throwable throwable ) {
		this( file, throwable.getMessage(), throwable );
	}

	public ReTestIOException( final File file, final String details, final Throwable throwable ) {
		super( "Error reading from file '" + FileUtil.canonicalPathQuietly( file ) + "': " + details, throwable );
		this.file = file;
	}

	public File getFile() {
		return file;
	}
}
