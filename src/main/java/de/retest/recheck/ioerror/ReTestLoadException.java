package de.retest.recheck.ioerror;

import java.net.URI;

public class ReTestLoadException extends ReTestIOException {

	private static final long serialVersionUID = 1L;

	public ReTestLoadException( final URI location, final Throwable throwable ) {
		super( location, throwable );
	}

	public ReTestLoadException( final URI location, final String details ) {
		super( location, details );
	}

	public ReTestLoadException( final URI location, final String details, final Throwable throwable ) {
		super( location, details, throwable );
	}
}
