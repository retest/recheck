package de.retest.recheck.ignore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.review.ignore.io.Loaders;

public class Filters {

	public static Filter load( final Path path ) throws IOException {
		try ( final Stream<String> filterFileLines = Files.lines( path ) ) {
			return Loaders.load( filterFileLines ) //
					.filter( Filter.class::isInstance ) //
					.map( Filter.class::cast ) //
					.collect( Collectors.collectingAndThen( Collectors.toList(), CompoundFilter::new ) );
		}
	}
}
