package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

class SearchFilterFilesTest {

	@Test
	void getDefaultFilterFiles_should_get_all_filter_files_from_classpath() {
		final List<File> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		assertThat( defaultFilterFiles.stream().map( File::getName ) ).contains( "positioning.filter",
				"visibility.filter" );
	}
}
