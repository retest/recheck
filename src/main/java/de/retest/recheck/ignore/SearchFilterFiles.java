package de.retest.recheck.ignore;

import static de.retest.recheck.Properties.RETEST_FOLDER_NAME;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
	public static final String FILTER_JS_EXTENSION = ".filter.js";
	public static final String FILTER_DIR_NAME = "filter";
	public static final String WEB_FILTER_DIR_PATH = FILTER_DIR_NAME + "/web";

	private static final List<String> defaultWebFilter = Arrays.asList( "positioning.filter", "style-attributes.filter",
			"invisible-attributes.filter", "content.filter" );

	private SearchFilterFiles() {}

	/**
	 * @return The default filter files from the JAR.
	 */
	public static List<Pair<String, FilterLoader>> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( SearchFilterFiles::getWebFilterResource ) //
				.filter( Objects::nonNull ) //
				.map( SearchFilterFiles::loadFilterFromUrl ) //
				.filter( Objects::nonNull ) //
				.collect( Collectors.toList() );
	}

	private static URL getWebFilterResource( final String webFilterName ) {
		final String webFilterResource = "/" + WEB_FILTER_DIR_PATH + "/" + webFilterName;
		return SearchFilterFiles.class.getResource( webFilterResource );
	}

	private static Pair<String, FilterLoader> loadFilterFromUrl( final URL url ) {
		final URI uri = URI.create( url.toExternalForm() );
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
			log.error( "Could not load filter at '{}'", uri, e );
			return null;
		}
	}

	/**
	 * @return The project filter files from the filter folder.
	 */
	public static List<Pair<String, FilterLoader>> getProjectFilterFiles() {
		return ProjectConfiguration.getInstance().getProjectConfigFolder() //
				.map( path -> path.resolve( FILTER_DIR_NAME ) ) //
				.map( SearchFilterFiles::loadFiltersFromDirectory ) //
				.orElse( Collections.emptyList() );
	}

	/**
	 * @return The user filter files from the user's home.
	 */
	public static List<Pair<String, FilterLoader>> getUserFilterFiles() {
		final Path userFilterFolder =
				Paths.get( System.getProperty( "user.home" ), RETEST_FOLDER_NAME, FILTER_DIR_NAME );
		if ( Files.exists( userFilterFolder ) ) {
			return loadFiltersFromDirectory( userFilterFolder );
		}
		return Collections.emptyList();
	}

	private static List<Pair<String, FilterLoader>> loadFiltersFromDirectory( final Path directory ) {
		try ( final Stream<Path> paths = Files.walk( directory ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( SearchFilterFiles::isFilterFile ) //
					.map( path -> Pair.of( getFileName( path ), FilterLoader.load( path ) ) ) //
					.collect( Collectors.toList() ); //
		} catch ( final NoSuchFileException e ) {
			log.warn( "No filter folder found at '{}': {}", directory, e.getMessage() );
		} catch ( final IOException e ) {
			log.error( "Exception accessing project filter folder '{}'.", directory, e );
		}
		return Collections.emptyList();
	}

	private static boolean isFilterFile( final Path path ) {
		final String fileName = getFileName( path );
		return fileName.endsWith( FILTER_EXTENSION ) || fileName.endsWith( FILTER_JS_EXTENSION );
	}

	/**
	 * @return Mapping from file names to filter. In the case of duplicates, specific filters are preferred over generic
	 *         filters (i.e. project filer over user filter over default filter).
	 */
	public static Map<String, Filter> toFileNameFilterMapping() {
		// Concat from specific to generic.
		return Stream.of( getProjectFilterFiles(), getUserFilterFiles(), getDefaultFilterFiles() )
				.flatMap( List::stream ) //
				.collect( Collectors.toMap(
						// Use the file name as key.
						Pair::getLeft,
						// Use the loaded filter as value.
						pair -> {
							final FilterLoader loader = pair.getRight();
							try {
								return loader.load();
							} catch ( final IOException e ) {
								log.error( "Could not load filter for name '{}'.", pair.getLeft(), e );
								return Filter.FILTER_NOTHING;
							}
						},
						// Prefer specific over generic filters (due to concat order).
						( specificFilter, genericFilter ) -> specificFilter ) );
	}

	private static String getFileName( final Path path ) {
		final Path fileName = path.getFileName();
		return fileName != null ? fileName.toString() : "n/a";
	}

	public static Filter getFilterByName( final String name ) {
		final Filter filter = toFileNameFilterMapping().get( name );
		if ( filter == null ) {
			throw ProjectConfiguration.getInstance().getProjectConfigFolder() //
					.map( path -> path.resolve( FILTER_DIR_NAME ) ) //
					.map( Path::toAbsolutePath ) //
					.map( path -> new FilterNotFoundException( name, path ) )
					.orElseGet( () -> new FilterNotFoundException( name ) );
		}
		return filter;
	}
}
