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

	/**
	 * @return The predefined filter files from the JAR.
	 */
	public static List<Pair<String, FilterLoader>> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( SearchFilterFiles.class::getResource ) //
				.filter( Objects::nonNull ) //
				.map( URL::toExternalForm ) //
				.map( URI::create ) //
				.map( SearchFilterFiles::loadFilterFromUri ) //
				.filter( Objects::nonNull ) //
				.collect( Collectors.toList() );
	}

	private static Pair<String, FilterLoader> loadFilterFromUri( final URI uri ) {
		try {
			final Path path = Paths.get( uri );
			return Pair.of( getFileName( path ), FilterLoader.load( path ) );
		} catch ( final FileSystemNotFoundException e ) {
			return createFileSystemAndLoadFilter( uri );
		}
	}

	private static Pair<String, FilterLoader> createFileSystemAndLoadFilter( final URI uri ) {
		try ( final FileSystem fs = FileSystems.newFileSystem( uri, Collections.emptyMap() ) ) {
			final Path path = fs.provider().getPath( uri );
			return Pair.of( getFileName( path ), FilterLoader.provide( path ) );
		} catch ( final IOException e ) {
			log.error( "Could not load Filter at '{}'", uri, e );
			return null;
		}
	}

	/**
	 * @return The user-defined filter files from the filter folder.
	 */
	public static List<Pair<String, FilterLoader>> getProjectFilterFiles() {
		return ProjectConfiguration.getInstance().getProjectConfigFolder() //
				.map( path -> path.resolve( FILTER_FOLDER ) ) //
				.map( SearchFilterFiles::loadFiltersFromDirectory ) //
				.orElse( Collections.emptyList() );
	}

	private static List<Pair<String, FilterLoader>> loadFiltersFromDirectory( final Path directory ) {
		try ( final Stream<Path> paths = Files.walk( directory ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( file -> file.toString().endsWith( FILTER_EXTENSION ) ) //
					.map( path -> Pair.of( getFileName( path ), FilterLoader.load( path ) ) ) //
					.collect( Collectors.toList() ); //
		} catch ( final IOException e ) {
			log.error( "Exception accessing user filter folder '{}'.", directory, e );
			return Collections.emptyList();
		}
	}

	public static Map<String, Filter> toFileNameFilterMapping() {
		final List<Pair<String, FilterLoader>> projectFilterFiles = getProjectFilterFiles();
		final List<Pair<String, FilterLoader>> defaultFilterFiles = getDefaultFilterFiles();
		return Stream.concat( projectFilterFiles.stream(), defaultFilterFiles.stream() ) //
				.collect( Collectors.toMap(
						// Use the file name as key.
						Pair::getLeft,
						// Use the loaded filter as value.
						pair -> {
							final FilterLoader loader = pair.getRight();
							try {
								return loader.load();
							} catch ( final IOException e ) {
								log.error( "Could not load Filter for '{}'.", pair.getLeft(), e );
								return Filter.FILTER_NOTHING;
							}
						},
						// Prefer project over default files (due to concat order).
						( projectFilter, defaultFilter ) -> projectFilter ) );
	}

	private static String getFileName( final Path path ) {
		return path.getFileName().toString();
	}
}
