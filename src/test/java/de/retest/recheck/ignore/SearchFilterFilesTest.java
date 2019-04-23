package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Test;

class SearchFilterFilesTest {

	@Test
	void getDefaultFilterFiles_should_get_all_filter_files_from_classpath() {
		final List<File> defaultFilterFiles = SearchFilterFiles.getDefaultFilterFiles();
		assertThat( defaultFilterFiles.stream().map( File::getName ) ).contains( "positioning.filter",
				"visibility.filter" );
	}

	@Test
	void getCostumerFilterFiles_should_only_get_filter_files() throws Exception {
		final File randomFile =
				Files.createTempFile( SearchFilterFiles.COSTUMER_FILTER_FOLDER, "random", ".ignore" ).toFile();
		final File colorFilter =
				Files.createTempFile( SearchFilterFiles.COSTUMER_FILTER_FOLDER, "color", ".filter" ).toFile();
		final File webFontFilter =
				Files.createTempFile( SearchFilterFiles.COSTUMER_FILTER_FOLDER, "web-font", ".filter" ).toFile();

		final List<File> costumerFilterFiles = SearchFilterFiles.getCostumerFilterFiles();
		assertThat( costumerFilterFiles ).allMatch( file -> file.toString().endsWith( ".filter" ) );
		assertThat( costumerFilterFiles.stream().map( File::getName ) ).contains( colorFilter.getName().toString(),
				webFontFilter.getName().toString() );

		colorFilter.deleteOnExit();
		webFontFilter.deleteOnExit();
		randomFile.deleteOnExit();
	}
}
