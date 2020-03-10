package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static de.retest.recheck.ignore.PersistentFilter.unwrap;
import static java.nio.file.Files.write;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;

import de.retest.recheck.RecheckProperties;

class SearchFilterFilesTest {

	Path retestFolder;
	Path filterFolder;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws IOException {
		retestFolder = temp.resolve( RecheckProperties.RETEST_FOLDER_NAME );
		filterFolder = retestFolder.resolve( SearchFilterFiles.FILTER_DIR_NAME );
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
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
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
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
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
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
	@ClearSystemProperty( key = "user.home" )
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
		final Filter filter = ((CompoundFilter) mapping.get( posFilterFileName )).getFilters().get( 0 );
		assertThat( ((PersistentFilter) filter).getFilter() ).hasToString( "#projectFilter" );
	}

	@Test
	@ClearSystemProperty( key = "user.home" )
	void filter_mapping_should_prefer_user_over_default_filters( @TempDir final Path temp ) throws Exception {
		final String posFilterFileName = "positioning.filter";
		final String posFilterInFolder = ".retest/filter/" + posFilterFileName;

		System.setProperty( "user.home", temp.toString() );
		final Path userPosFilterPath = temp.resolve( posFilterInFolder );
		write( userPosFilterPath, "#userFilter".getBytes() );

		final Map<String, Filter> mapping = SearchFilterFiles.toFileNameFilterMapping();
		final CompoundFilter filter = (CompoundFilter) mapping.get( posFilterFileName );
		assertThat( unwrap( filter.getFilters() ) ).hasToString( "[#userFilter]" );
	}

	@Test
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
	void getFilterByName_should_return_user_filter_with_given_name_over_default_filter() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );
		final Path positioningFilter = filterFolder.resolve( "content.filter" );
		Files.createFile( positioningFilter );

		final Filter result = SearchFilterFiles.getFilterByName( "content.filter" );
		assertThat( result ).isNotNull();
		assertThat( result.toString() ).isEqualTo( "CompoundFilter(filters=[])" );
	}

	@Test
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
	void getFilterByName_should_only_load_searched_filter() throws IOException {
		System.setProperty( RETEST_PROJECT_ROOT, filterFolder.toString() );

		// Create infinite filter loop which will cause infinite loop if loaded
		final Path foo = filterFolder.resolve( "foo.filter" );
		Files.write( foo, Collections.singleton( "import: bar.filter" ) );
		final Path bar = filterFolder.resolve( "bar.filter" );
		Files.write( bar, Collections.singleton( "import: foo.filter" ) );

		final Path positioningFilter = filterFolder.resolve( "content.filter" );
		Files.createFile( positioningFilter );

		final Filter result = SearchFilterFiles.getFilterByName( "content.filter" );
		assertThat( result ).isInstanceOfSatisfying( CompoundFilter.class,
				compound -> assertThat( compound.getFilters() ).isEmpty() );
	}

	@Test
	void getFilterByName_should_return_default_filter_with_given_name() {
		final Filter result = SearchFilterFiles.getFilterByName( "content.filter" );
		final List<Filter> list = unwrap( ((CompoundFilter) result).getFilters() );
		assertThat( result ).isNotNull();
		assertThat( list.toString() ).startsWith( "[# Filter file for recheck that will filter content" );
	}
}
