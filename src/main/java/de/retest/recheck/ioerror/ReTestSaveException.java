package de.retest.recheck.ioerror;

import java.net.URI;

public class ReTestSaveException extends ReTestIOException {

	private static final long serialVersionUID = 1L;

	public ReTestSaveException( final URI location, final Throwable throwable ) {
		super( location, throwable );
	}

	public ReTestSaveException( final URI location, final String details, final Throwable throwable ) {
		super( location, details, throwable );
	}
}
