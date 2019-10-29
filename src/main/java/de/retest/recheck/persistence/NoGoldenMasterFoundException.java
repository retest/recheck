package de.retest.recheck.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoGoldenMasterFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final List<String> filenames;

	public NoGoldenMasterFoundException( final String... filenames ) {
		super( "No Golden Master file(s) with the following name(s) found:\n" + listFilenames( filenames ) );
		this.filenames = Arrays.asList( filenames );
	}

	private static String listFilenames( final String[] filenames ) {
		return Stream.of( filenames ).collect( Collectors.joining( "\n\t", "\t", "" ) );
	}

	public List<String> getFilenames() {
		return Collections.unmodifiableList( filenames );
	}
}
