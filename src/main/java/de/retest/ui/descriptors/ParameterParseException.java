package de.retest.ui.descriptors;

public class ParameterParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParameterParseException( final String msg, final Exception exc ) {
		super( msg, exc );
	}

}
