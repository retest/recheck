package de.retest.ui;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException( final String message ) {
		super( message );
	}

	public NotFoundException( final Throwable cause ) {
		super( cause );
	}

	public NotFoundException( final String message, final Throwable cause ) {
		super( message, cause );
	}

}
