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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
		final List<Pair<Path, FilterLoader>> allFilterFiles = SearchFilterFiles.getAllFilterFiles();
		assertThat( allFilterFiles.stream().map( path -> path.getLeft().getFileName().toString() ) )
				.contains( "positioning.filter", "visibility.filter", colorFilter.toPath().getFileName().toString() );
	}

	@Test
	void getDefaultFilterFiles_should_get_all_filter_files_from_classpath() {
		final List<Pair<Path, FilterLoader>> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		assertThat( defaultFilterFiles.stream().map( path -> path.getLeft().getFileName().toString() ) )
				.contains( "positioning.filter", "visibility.filter" );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getProjectFilterFiles_should_only_get_filter_files() throws IOException {
		Files.createTempFile( filterFolder, "random", ".ignore" ).toFile();
		final File colorFilter = Files.createTempFile( filterFolder, "color", FILTER_EXTENSION ).toFile();
		final File webFontFilter = Files.createTempFile( filterFolder, "web-font", FILTER_EXTENSION ).toFile();
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );

		final List<Pair<Path, FilterLoader>> projectFilterFiles = SearchFilterFiles.getProjectFilterFiles();
		assertThat( projectFilterFiles ).allMatch( pair -> pair.getLeft().toString().endsWith( FILTER_EXTENSION ) );
		assertThat( projectFilterFiles.stream().map( Pair::getLeft ).map( Path::getFileName ) )
				.contains( colorFilter.toPath().getFileName(), webFontFilter.toPath().getFileName() );
	}

	@Test
	void loadFilterMap_should_contain_all_existing_filter_files() {
		final List<Pair<Path, FilterLoader>> allFilter = SearchFilterFiles.getAllFilterFiles();
		final Map<Path, Filter> filterMap = SearchFilterFiles.toPathFilterMapping( allFilter );
		assertThat( allFilter.stream().map( Pair::getLeft ) ).containsAll( filterMap.keySet() );
	}
}
