package de.retest.recheck.ignore;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.configuration.ProjectConfiguration;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFilterFiles {

	public static final String FILTER_EXTENSION = ".filter";
	public static final String FILTER_JS_EXTENSION = ".filter.js";
	public static final String FILTER_DIR_NAME = "filter";

	private static final String WEB_CATEGORY = "web";

	/**
	 * @return The default filter files from the JAR.
	 */
	@Getter( lazy = true )
	private static final List<Pair<String, FilterLoader>> defaultFilterFiles = Stream.of( //
			FilterResource.prefix( WEB_CATEGORY, "content.filter" ), //
			FilterResource.prefix( WEB_CATEGORY, "invisible-attributes.filter" ), //
			FilterResource.prefix( WEB_CATEGORY, "positioning.filter" ), //
			FilterResource.prefix( WEB_CATEGORY, "style-attributes.filter" ), //

			FilterResource.absolute( "metadata.filter" ) //
	) //
			.map( FilterResource::loader ) //
			.collect( Collectors.toList() );

	private SearchFilterFiles() {}

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
		if ( userFilterFolder.toFile().exists() ) {
			return loadFiltersFromDirectory( userFilterFolder );
		}
		return Collections.emptyList();
	}

	private static List<Pair<String, FilterLoader>> loadFiltersFromDirectory( final Path directory ) {
		try ( final Stream<Path> paths = Files.walk( directory ) ) {
			return paths.filter( path -> path.toFile().isFile() ) //
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
		return toFileLoaderMapping().entrySet().stream() //
				.map( entry -> {
					final String name = entry.getKey();
					final FilterLoader loader = entry.getValue();
					return Pair.of( name, loadSilently( loader, name ) );
				} ) //
				.collect( Collectors.toMap( Pair::getLeft, Pair::getRight ) );
	}

	private static Map<String, FilterLoader> toFileLoaderMapping() {
		return Stream.of( getProjectFilterFiles(), getUserFilterFiles(), getDefaultFilterFiles() )
				.flatMap( List::stream ) //
				.collect( Collectors.toMap(
						// Use the file name as key.
						Pair::getLeft,
						// Use the loaded filter as value.
						Pair::getRight,
						// Prefer specific over generic filters (due to concat order).
						( specificFilterLoader, genericFilterLoader ) -> specificFilterLoader ) );
	}

	private static Filter loadSilently( final FilterLoader loader, final String name ) {
		try {
			return loader.load();
		} catch ( final IOException e ) {
			log.error( "Could not load filter for name '{}'.", name, e );
			return Filter.NEVER_MATCH;
		}
	}

	private static String getFileName( final Path path ) {
		final Path fileName = path.getFileName();
		return fileName != null ? fileName.toString() : "n/a";
	}

	public static Filter getFilterByName( final String name ) {
		final FilterLoader loader = toFileLoaderMapping().get( name );
		if ( loader == null ) {
			throw ProjectConfiguration.getInstance().getProjectConfigFolder() //
					.map( path -> path.resolve( FILTER_DIR_NAME ) ) //
					.map( Path::toAbsolutePath ) //
					.map( Path::toString ) //
					.map( s -> new FilterNotFoundException( name, s ) )
					.orElseGet( () -> new FilterNotFoundException( name ) );
		}
		return loadSilently( loader, name );
	}

	@Value( staticConstructor = "of" )
	static class FilterResource {

		private final String name;
		private final String path;

		static FilterResource absolute( final String path ) {
			return of( path, "/" + String.join( "/", FILTER_DIR_NAME, path ) );
		}

		static FilterResource prefix( final String category, final String path ) {
			return of( path, "/" + String.join( "/", FILTER_DIR_NAME, category, path ) );
		}

		public Pair<String, FilterLoader> loader() {
			return Pair.of( name, FilterLoader.loadResource( path ) );
		}
	}
}
