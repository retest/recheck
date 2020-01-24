package de.retest.recheck.review.ignore.io;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.util.OptionalUtil;

public interface Loader<T> {

	Optional<T> load( String line );

	default Stream<T> load( final Stream<String> lines ) {
		return lines.map( this::load ) //
				.flatMap( OptionalUtil::stream );
	}

	default Stream<PersistentFilter> load( final Path path, final Stream<String> lines ) {
		return load( lines ).map( f -> new PersistentFilter( path, (Filter) f ) );
	}

	String save( T ignore );

	default Stream<String> save( final Stream<T> objects ) {
		return objects.map( this::save );
	}
}
