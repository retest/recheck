package de.retest.recheck.ioerror;

import java.io.File;

public class ReTestSaveException extends ReTestIOException {

	private static final long serialVersionUID = 1L;

	public ReTestSaveException( final File file, final Throwable throwable ) {
		super( file, throwable );
	}

	public ReTestSaveException( final File file, final String details, final Throwable throwable ) {
		super( file, details, throwable );
	}
}
