package de.retest.recheck.persistence;

public class NoStateFileFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String filename;

	public NoStateFileFoundException( final String filename ) {
		super( "No state file with name '" + filename + "' found!" );
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
