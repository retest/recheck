package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.review.ignore.io.Loaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFilterFiles {

	public static final String FILTER_EXTENSION = ".filter";
	private static final String BASIC_FILTER_DIR = "/filter/";
	private static final String WEB_FILTER_DIR = BASIC_FILTER_DIR + "web/";
	private static final List<String> defaultWebFilter =
			Arrays.asList( WEB_FILTER_DIR + "positioning.filter", WEB_FILTER_DIR + "visibility.filter" );

	private SearchFilterFiles() {}

	public static List<Path> getAllFilterFiles() {
		return Stream.concat( getDefaultFilterFiles().stream(), getProjectFilterFiles().stream() ) //
				.collect( Collectors.toList() );
	}

	public static List<Path> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( filter -> SearchFilterFiles.class.getResource( filter ) ) //
				.filter( Objects::nonNull ) //
				.map( resource -> Paths.get( URI.create( resource.toString() ) ) ) //
				.collect( Collectors.toList() ); //
	}

	public static List<Path> getProjectFilterFiles() {
		final Path resolveFilterPath =
				ProjectConfiguration.getInstance().findProjectConfigFolder().resolve( FILTER_FOLDER );
		if ( !resolveFilterPath.toFile().exists() ) {
			return Collections.emptyList();
		}
		try ( Stream<Path> paths = Files.walk( resolveFilterPath ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( file -> file.toString().endsWith( FILTER_EXTENSION ) ) //
					.collect( Collectors.toList() ); //
		} catch ( final IOException e ) {
			log.error( "Exception accessing user filter folder '{}'.", resolveFilterPath, e );
			return Collections.emptyList();
		}
	}

	private static Filter toFilter( final Path filter ) {
		try ( final Stream<String> filterFileLines = Files.lines( filter ) ) {
			return Loaders.load( filterFileLines ) //
					.filter( Filter.class::isInstance ) //
					.map( Filter.class::cast )//
					.collect( Collectors.collectingAndThen( Collectors.toList(), CompoundFilter::new ) );
		} catch ( final IOException e ) {
			throw new UncheckedIOException( "Could not load '" + filter + "' file.", e );
		}
	}
}
