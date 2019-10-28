package de.retest.recheck.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NoGoldenMasterFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final List<String> filenames;

	public NoGoldenMasterFoundException( final String... filenames ) {
		super( "No Golden Master with the following name(s) has/have been found: " + Arrays.toString( filenames ) );
		this.filenames = Arrays.asList( filenames );
	}

	public List<String> getFilenames() {
		return Collections.unmodifiableList( filenames );
	}
}
