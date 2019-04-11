package de.retest.recheck.persistence;

public class NoGoldenMasterFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String filename;

	public NoGoldenMasterFoundException( final String filename ) {
		super( "No Golden Master with the name '" + filename + "' has been found!" );
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
