package de.retest.recheck.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NoGoldenMasterFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final List<String> filenames;

	public NoGoldenMasterFoundException( final String... filenames ) {
		super( "No Golden Master file(s) with the following name(s) found: \n"
				+ Arrays.asList( filenames ).stream().collect( Collectors.joining( "\n" ) ) );
		this.filenames = Arrays.asList( filenames );
	}

	public List<String> getFilenames() {
		return Collections.unmodifiableList( filenames );
	}
}
