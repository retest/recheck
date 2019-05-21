package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.configuration.ProjectConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFilterFiles {

	public static final String FILTER_EXTENSION = ".filter";
	private static final String BASIC_FILTER_DIR = "/filter/";
	private static final String WEB_FILTER_DIR = BASIC_FILTER_DIR + "web/";
	private static final List<String> defaultWebFilter =
			Arrays.asList( WEB_FILTER_DIR + "positioning.filter", WEB_FILTER_DIR + "visibility.filter" );

	private SearchFilterFiles() {}

	public static List<Pair<Path, FilterLoader>> getAllFilterFiles() {
		return Stream.concat( getDefaultFilterFiles().stream(), getProjectFilterFiles().stream() ) //
				.collect( Collectors.toList() );
	}

	public static List<Pair<Path, FilterLoader>> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( SearchFilterFiles.class::getResource ) //
				.filter( Objects::nonNull ) //
				.map( URL::toExternalForm ) //
				.map( URI::create ) //
				.map( SearchFilterFiles::loadFilterFromUri ) //
				.filter( Objects::nonNull ) //
				.collect( Collectors.toList() );
	}

	private static Pair<Path, FilterLoader> loadFilterFromUri( final URI uri ) {
		try {
			final Path path = Paths.get( uri );
			return Pair.of( path, FilterLoader.load( path ) );
		} catch ( final FileSystemNotFoundException e ) {
			try ( final FileSystem fs = FileSystems.newFileSystem( uri, Collections.emptyMap() ) ) {
				final Path path = fs.provider().getPath( uri );
				return Pair.of( path, FilterLoader.provide( path ) );
			} catch ( final IOException ex ) {
				log.error( "Could not load Filter at '{}'", uri, ex );
				return null;
			}
		}
	}

	public static List<Pair<Path, FilterLoader>> getProjectFilterFiles() {
		final Path resolveFilterPath =
				ProjectConfiguration.getInstance().findProjectConfigFolder().resolve( FILTER_FOLDER );
		if ( !resolveFilterPath.toFile().exists() ) {
			return Collections.emptyList();
		}
		try ( final Stream<Path> paths = Files.walk( resolveFilterPath ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( file -> file.toString().endsWith( FILTER_EXTENSION ) ) //
					.map( path -> Pair.of( path, FilterLoader.load( path ) ) ) //
					.collect( Collectors.toList() ); //
		} catch ( final IOException e ) {
			log.error( "Exception accessing user filter folder '{}'.", resolveFilterPath, e );
			return Collections.emptyList();
		}
	}

	public static Map<Path, Filter> toPathFilterMapping( final List<Pair<Path, FilterLoader>> paths ) {
		return paths.stream() //
				.collect( Collectors.toMap( Pair::getLeft, pair -> {
					final FilterLoader loader = pair.getRight();
					try {
						return loader.load();
					} catch ( final IOException e ) {
						log.error( "Could not load Filter for '{}'.", pair.getLeft() );
						return Filter.FILTER_NOTHING;
					}
				} ) );
	}
}
