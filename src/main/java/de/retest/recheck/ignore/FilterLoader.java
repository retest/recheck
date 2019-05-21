package de.retest.recheck.ignore;

import java.io.IOException;
import java.nio.file.Path;

public interface FilterLoader {

	Filter load() throws IOException;

	static FilterLoader load( final Path path ) {
		return () -> Filters.load( path );
	}

	static FilterLoader provide( final Path path ) throws IOException {
		return provide( Filters.load( path ) );
	}

	static FilterLoader provide( final Filter filter ) {
		return () -> filter;
	}
}
