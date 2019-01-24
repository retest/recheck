package de.retest.ui;

public class NoSuchElementException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	public NoSuchElementException( final String message ) {
		super( message );
	}

	public NoSuchElementException( final Throwable cause ) {
		super( cause );
	}

	public NoSuchElementException( final String message, final Throwable cause ) {
		super( message, cause );
	}

}
