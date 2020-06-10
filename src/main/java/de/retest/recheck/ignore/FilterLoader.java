package de.retest.recheck.ignore;

import static de.retest.recheck.ignore.SearchFilterFiles.FILTER_JS_EXTENSION;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

public interface FilterLoader {

	Filter load() throws IOException;

	static FilterLoader loadResource( final String path ) {
		return () -> {
			try ( final InputStream stream = FilterLoader.class.getResourceAsStream( path ) ) {
				if ( stream == null ) {
					throw new NoSuchFileException( path );
				}
				return Filters.parse( IOUtils.readLines( stream, StandardCharsets.UTF_8 ) );
			}
		};
	}

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
