package de.retest.recheck.ioerror;

import java.io.IOException;
import java.net.URI;

public class ReTestIOException extends IOException {

	private static final long serialVersionUID = 1L;
	private final URI location;

	public ReTestIOException( final URI location, final Throwable throwable ) {
		this( location, throwable.getMessage(), throwable );
	}

	public ReTestIOException( final URI location, final String details ) {
		super( "Error reading from '" + location + "': " + details );
		this.location = location;
	}

	public ReTestIOException( final URI location, final String details, final Throwable throwable ) {
		super( "Error reading from '" + location + "': " + details, throwable );
		this.location = location;
	}

	public URI getLocation() {
		return location;
	}
}
