package de.retest.recheck.review.ignore.io;

import java.util.Optional;
import java.util.stream.Stream;

import de.retest.recheck.util.StreamUtil;

public interface Loader<T> {

	Optional<T> load( String line );

	default Stream<T> load( final Stream<String> lines ) {
		return lines.map( this::load ) //
				.flatMap( StreamUtil::optionalToStream );
	}

	String save( T ignore );

	default Stream<String> save( final Stream<T> objects ) {
		return objects.map( this::save );
	}
}
