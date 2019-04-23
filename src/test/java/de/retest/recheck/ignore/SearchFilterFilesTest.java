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
		assertThat( defaultFilterFiles.stream().map( File::getName ) ).contains( "positioning.filter",
				"visibility.filter" );
	}
}
