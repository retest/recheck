package de.retest.recheck.ignore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.review.ignore.io.Loaders;

public class Filters {

	private Filters() {}

	public static Filter load( final Path path ) throws IOException {
		try ( final Stream<String> filterFileLines = Files.lines( path ) ) {
			return parse( path, filterFileLines );
		}
	}

	public static Filter parse( final String line ) {
		return parse( Stream.of( line ) );
	}

	public static Filter parse( final List<String> lines ) {
		return parse( lines.stream() );
	}

	public static Filter parse( final Stream<String> lines ) {
		return new CompoundFilter( Loaders.filter().load( lines ).collect( Collectors.toList() ) );
	}

	public static Filter parse( final Path path, final Stream<String> lines ) {
		return new CompoundFilter( Loaders.filter().load( path, lines ).collect( Collectors.toList() ) );
	}
}
