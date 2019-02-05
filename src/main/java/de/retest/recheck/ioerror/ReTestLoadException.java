package de.retest.recheck.ioerror;

import java.io.File;

public class ReTestLoadException extends ReTestIOException {

	private static final long serialVersionUID = 1L;

	public ReTestLoadException( final File file, final Throwable throwable ) {
		super( file, throwable );
	}
}
