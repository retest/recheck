package de.retest.recheck.ignore;

import static de.retest.recheck.ignore.SearchFilterFiles.FILTER_JS_EXTENSION;

import java.io.IOException;
import java.nio.file.Path;

public interface FilterLoader {

	Filter load() throws IOException;

	static FilterLoader load( final Path path ) {
		if ( path.toString().toLowerCase().endsWith( FILTER_JS_EXTENSION ) ) {
			return () -> new JSFilterImpl( path );
		}
		return () -> Filters.load( path );
	}

	static FilterLoader provide( final Path path ) throws IOException {
		final Filter filter = Filters.load( path );
		return () -> filter;
	}
}
