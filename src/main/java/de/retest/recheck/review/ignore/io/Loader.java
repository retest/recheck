package de.retest.recheck.review.ignore.io;

public interface Loader<T> {

	default boolean canLoad( final String line ) {
		return true;
	}

	T load( String line );

	String save( T ignore );
}
