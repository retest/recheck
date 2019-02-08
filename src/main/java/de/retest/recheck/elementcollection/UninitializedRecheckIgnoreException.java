package de.retest.recheck.elementcollection;

public class UninitializedRecheckIgnoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UninitializedRecheckIgnoreException( final String msg ) {
		super( msg );
	}
}
