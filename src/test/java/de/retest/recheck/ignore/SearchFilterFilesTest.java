package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

class SearchFilterFilesTest {
	SearchFilterFiles cut;

	@Test
	void getDefaultFilterFiles_should_get_all_filter_files_from_classpath() {
		final SearchFilterFiles cut = new SearchFilterFiles();
		final List<File> defaultFilterFiles = cut.getDefaultFilterFiles();
		assertThat( defaultFilterFiles ).isNotNull();
		assertThat( defaultFilterFiles ).isNotEmpty();
		assertThat( defaultFilterFiles ).hasSize( 2 );
		assertThat( defaultFilterFiles ).allMatch( file -> file.toString().endsWith( ".filter" ) );
		assertThat( defaultFilterFiles.get( 0 ).getName() ).isEqualTo( "positioning.filter" );
		assertThat( defaultFilterFiles.get( 1 ).getName() ).isEqualTo( "visibility.filter" );
	}
}
