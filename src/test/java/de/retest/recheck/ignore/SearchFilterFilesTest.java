package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.configuration.ProjectConfiguration;
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

	@AfterAll
	static void tearDown() {
		ProjectConfiguration.getInstance().resetConfiguration();
	}

	@Test
	void getDefaultFilterFiles_should_get_all_default_filters() {
		final List<Pair<String, FilterLoader>> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		final List<String> actualFilterFileNames = defaultFilterFiles.stream() //
				.map( Pair::getLeft ) //
				.collect( Collectors.toList() );
		final List<String> expectedFilterFileNames =
				Arrays.asList( "positioning.filter", "style-attributes.filter", "invisible-attributes.filter" );
		assertThat( actualFilterFileNames ).isEqualTo( expectedFilterFileNames );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getProjectFilterFiles_should_get_all_project_filters() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		ProjectConfiguration.getInstance().resetConfiguration();
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
	void filter_mapping_should_prefer_project_over_default_filters() throws Exception {
		final String posFilterFileName = "positioning.filter";

		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final Path projectPosFilterPath = filterFolder.resolve( posFilterFileName );
		Files.createFile( projectPosFilterPath );

		final List<Pair<String, FilterLoader>> projectFilterFiles = SearchFilterFiles.getProjectFilterFiles();
		final CompoundFilter projectPosFilter = (CompoundFilter) projectFilterFiles.get( 0 ).getRight().load();
		assertThat( projectPosFilter.getFilters() ).isEmpty();

		final List<Pair<String, FilterLoader>> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		final CompoundFilter defaultPosFilter = (CompoundFilter) defaultFilterFiles.get( 0 ).getRight().load();
		assertThat( defaultPosFilter.getFilters() ).isNotEmpty();

		// Filter has no equals, so we have to it this way.
		final Map<String, Filter> mapping = SearchFilterFiles.toFileNameFilterMapping();
		final CompoundFilter actualPositioningFilter = (CompoundFilter) mapping.get( posFilterFileName );
		assertThat( actualPositioningFilter.getFilters() ).isEmpty();
	}
}
