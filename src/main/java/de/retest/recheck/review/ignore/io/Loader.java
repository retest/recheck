package de.retest.recheck.review.ignore.io;

public interface Loader<T> {

	boolean canLoad( final String line );

	T load( String line );

	String save( T ignore );
}
