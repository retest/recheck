package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static de.retest.recheck.ignore.SearchFilterFiles.FILTER_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

class SearchFilterFilesTest {

	private Path filterFolder;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws IOException {
		final Path configFolder = temp.resolve( RETEST_PROJECT_ROOT );
		Files.createDirectory( configFolder );
		final Path retestFolder = configFolder.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		Files.createDirectory( retestFolder );
		filterFolder = retestFolder.resolve( FILTER_FOLDER );
		Files.createDirectory( filterFolder );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getAllFilterFiles_should_find_all_filters() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final File colorFilter = Files.createTempFile( filterFolder, "color", FILTER_EXTENSION ).toFile();
		final List<Path> allFilterFiles = SearchFilterFiles.getAllFilterFiles();
		assertThat( allFilterFiles.stream().map( path -> path.getFileName().toString() ) )
				.contains( "positioning.filter", "visibility.filter", colorFilter.toPath().getFileName().toString() );
	}

	@Test
	void getDefaultFilterFiles_should_get_all_filter_files_from_classpath() {
		final List<Path> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		assertThat( defaultFilterFiles.stream().map( path -> path.getFileName().toString() ) )
				.contains( "positioning.filter", "visibility.filter" );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getProjectFilterFiles_should_only_get_filter_files() throws IOException {
		Files.createTempFile( filterFolder, "random", ".ignore" ).toFile();
		final File colorFilter = Files.createTempFile( filterFolder, "color", FILTER_EXTENSION ).toFile();
		final File webFontFilter = Files.createTempFile( filterFolder, "web-font", FILTER_EXTENSION ).toFile();
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );

		final List<Path> projectFilterFiles = SearchFilterFiles.getProjectFilterFiles();
		assertThat( projectFilterFiles ).allMatch( file -> file.toString().endsWith( FILTER_EXTENSION ) );
		assertThat( projectFilterFiles.stream().map( Path::getFileName ) ).contains( colorFilter.toPath().getFileName(),
				webFontFilter.toPath().getFileName() );
	}

	@Test
	void searchFilterByName_should_return_filter_file() throws IOException {
		final String name = "positioning.filter";
		final File positioningFile = Paths.get( "src/main/resources/filter/web/positioning.filter" ).toFile();
		final Optional<Filter> filter = SearchFilterFiles.searchFilterByName( name );
		final Stream<String> ignoreFileLines = Files.lines( Paths.get( positioningFile.getPath() ) );
		final Filter ignoreApplier = Loaders.load( ignoreFileLines ) //
				.filter( Filter.class::isInstance ) //
				.map( Filter.class::cast ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), CompoundFilter::new ) );
		assertThat( filter.get() ).isEqualToComparingFieldByFieldRecursively( ignoreApplier );
	}

	@Test
	void searchFilterByName_should_not_return_nonexistent_file() throws IOException {
		final String invalidName = "color.filter";
		final Optional<Filter> invalidFilter = SearchFilterFiles.searchFilterByName( invalidName );
		assertThat( invalidFilter ).isEqualTo( Optional.empty() );
	}
}
