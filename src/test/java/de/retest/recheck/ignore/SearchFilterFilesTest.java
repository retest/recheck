package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static java.nio.file.Files.write;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
	void getDefaultFilterFiles_should_get_all_default_filters() {
		final List<Pair<String, FilterLoader>> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		final List<String> actualFilterFileNames = defaultFilterFiles.stream() //
				.map( Pair::getLeft ) //
				.collect( Collectors.toList() );
		final List<String> expectedFilterFileNames = Arrays.asList( "positioning.filter", "style-attributes.filter",
				"invisible-attributes.filter", "content.filter" );
		assertThat( actualFilterFileNames ).isEqualTo( expectedFilterFileNames );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getProjectFilterFiles_should_get_all_project_filters() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final Path someFilter = filterFolder.resolve( "some.filter" );
		Files.createFile( someFilter );
		final Path anotherFilter = filterFolder.resolve( "another.filter.js" );
		Files.createFile( anotherFilter );

		final List<Pair<String, FilterLoader>> projectFilterFiles = SearchFilterFiles.getProjectFilterFiles();
		final List<String> actualFilterFileNames = projectFilterFiles.stream() //
				.map( Pair::getLeft ) //
				.collect( Collectors.toList() );
		final List<String> expectedFilterFileNames = Arrays.asList( "another.filter.js", "some.filter" );
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

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	@SystemProperty( key = "user.home" )
	void filter_mapping_should_prefer_project_over_user_filters( @TempDir final Path temp ) throws Exception {
		final String posFilterFileName = "positioning.filter";
		final String posFilterInFolder = ".retest/filter/" + posFilterFileName;

		final Path projectRoot = temp.resolve( "project" );
		System.setProperty( RETEST_PROJECT_ROOT, projectRoot.toString() );
		final Path projectPosFilterPath = projectRoot.resolve( posFilterInFolder );
		Files.createDirectories( projectPosFilterPath.getParent() );
		write( projectPosFilterPath, "#projectFilter".getBytes() );

		final Path userRoot = temp.resolve( "user" );
		System.setProperty( "user.home", userRoot.toString() );
		final Path userPosFilterPath = userRoot.resolve( posFilterInFolder );
		Files.createDirectories( userPosFilterPath.getParent() );
		write( userPosFilterPath, "#userFilter".getBytes() );

		final Map<String, Filter> mapping = SearchFilterFiles.toFileNameFilterMapping();
		final Filter filter = mapping.get( posFilterFileName );
		assertThat( filter ).hasToString( "CompoundFilter(filters=[#projectFilter])" );
	}

	@Test
	@SystemProperty( key = "user.home" )
	void filter_mapping_should_prefer_user_over_default_filters( @TempDir final Path temp ) throws Exception {
		final String posFilterFileName = "positioning.filter";
		final String posFilterInFolder = ".retest/filter/" + posFilterFileName;

		System.setProperty( "user.home", temp.toString() );
		final Path userPosFilterPath = temp.resolve( posFilterInFolder );
		write( userPosFilterPath, "#userFilter".getBytes() );

		final Map<String, Filter> mapping = SearchFilterFiles.toFileNameFilterMapping();
		final Filter filter = mapping.get( posFilterFileName );
		assertThat( filter ).hasToString( "CompoundFilter(filters=[#userFilter])" );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void getFilterByName_should_return_user_filter_with_given_name_over_default_filter() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final Path positioningFilter = filterFolder.resolve( "content.filter" );
		Files.createFile( positioningFilter );

		final Filter result = SearchFilterFiles.getFilterByName( "content.filter" );
		assertThat( result ).isNotNull();
		assertThat( result.toString() ).isEqualTo( "CompoundFilter(filters=[])" );
	}

	@Test
	void getFilterByName_should_return_default_filter_with_given_name() {
		final Filter result = SearchFilterFiles.getFilterByName( "content.filter" );
		assertThat( result ).isNotNull();
		assertThat( result.toString() ).isEqualTo(
				"CompoundFilter(filters=[# Filter file for recheck that will filter content, , attribute=text])" );
	}
}
