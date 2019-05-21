package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.util.junit.jupiter.SystemProperty;

class SearchFilterFilesTest {

	Path retestFolder;
	Path filterFolder;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws IOException {
		retestFolder = temp.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		filterFolder = retestFolder.resolve( FILTER_FOLDER );
		Files.createDirectories( filterFolder );
	}

	@Test
	void getDefaultFilterFiles_should_get_all_predefines_filters() {
		final List<Pair<String, FilterLoader>> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		final List<String> actualFilterFileNames = defaultFilterFiles.stream() //
				.map( Pair::getLeft ) //
				.collect( Collectors.toList() );
		final List<String> expectedFilterFileNames = Arrays.asList( "positioning.filter", "visibility.filter" );
		assertThat( actualFilterFileNames ).isEqualTo( expectedFilterFileNames );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getProjectFilterFiles_should_get_all_user_defined_filters() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final Path someFilter = filterFolder.resolve( "some.filter" );
		Files.createFile( someFilter );
		final Path anotherFilter = filterFolder.resolve( "another.filter" );
		Files.createFile( anotherFilter );

		final List<Pair<String, FilterLoader>> projectFilterFiles = SearchFilterFiles.getProjectFilterFiles();
		final List<String> actualFilterFileNames = projectFilterFiles.stream() //
				.map( Pair::getLeft ) //
				.collect( Collectors.toList() );
		final List<String> expectedFilterFileNames = Arrays.asList( "another.filter", "some.filter" );
		assertThat( actualFilterFileNames ).isEqualTo( expectedFilterFileNames );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void filter_mapping_should_prioritize_user_defined_filters() throws Exception {
		final Pair<String, FilterLoader> pair = Pair.of( "some.filter", () -> mock( Filter.class ) );
		final List<Pair<String, FilterLoader>> paths = Arrays.asList( pair, pair );
		SearchFilterFiles.toFileNameFilterMapping( paths );
	}
}
