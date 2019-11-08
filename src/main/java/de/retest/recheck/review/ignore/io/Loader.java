package de.retest.recheck.review.ignore.io;

import java.util.Optional;

public interface Loader<T> {

	Optional<T> load( String line );

	String save( T ignore );
}
