package de.retest.recheck.persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

public class NoGoldenMasterFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String GOLDEN_MASTER_PREFIX = "\n\t- ";

	private final List<String> filenames;

	public NoGoldenMasterFoundException( final String... filenames ) {
		super( "The following Golden Master(s) cannot be found:" + listFilenames( filenames ) );
		this.filenames = Arrays.asList( filenames );
	}

	private static String listFilenames( final String[] filenames ) {
		if ( ArrayUtils.isEmpty( filenames ) ) {
			throw new IllegalArgumentException( "You should at least provide one Golden Master file." );
		}
		return Stream.of( filenames ).collect( Collectors.joining( GOLDEN_MASTER_PREFIX, GOLDEN_MASTER_PREFIX, "" ) );
	}

	public List<String> getFilenames() {
		return Collections.unmodifiableList( filenames );
	}
}
