package de.retest.recheck.review.ignore.io;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static de.retest.recheck.ignore.SearchFilterFiles.FILTER_DIR_NAME;
import static de.retest.recheck.review.ignore.io.ImportExternalFilterLoader.IMPORT_STATEMENT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.ImportedExternalFilter;
import io.github.netmikey.logunit.api.LogCapturer;

class ImportExternalFilterLoaderIT {

	@TempDir
	Path temp;

	@RegisterExtension
	LogCapturer warningAndErrorLogs = LogCapturer.create() //
			.captureForType( ImportExternalFilterLoader.class );

	@Test
	void import_provided_filter_should_load_that_filter() {
		final String importLine = IMPORT_STATEMENT + "style-attributes.filter";
		final ImportExternalFilterLoader importer = new ImportExternalFilterLoader();
		final ImportedExternalFilter filter = importer.load( importLine ).get();

		assertThat( filter ).hasToString( importLine );
		assertThat( importer.save( filter ) ).isEqualTo( importLine );

		assertThat( filter.getReference() ).isEqualTo( "style-attributes.filter" );
	}

	@Test
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
	void import_existing_project_file_should_load_that_file() throws IOException {
		final Path projectRoot = temp.resolve( "project" );
		System.setProperty( RETEST_PROJECT_ROOT, projectRoot.toString() );

		final String reference = "my-filter.filter";
		final String fileContents = "# This is another comment";
		final Path tmp = projectRoot.resolve( Paths.get( RETEST_FOLDER_NAME, FILTER_DIR_NAME, reference ) );
		Files.createDirectories( tmp.getParent() );
		Files.write( tmp, fileContents.getBytes() );

		final String importLine = IMPORT_STATEMENT + reference;
		final ImportExternalFilterLoader importer = new ImportExternalFilterLoader();
		final ImportedExternalFilter filter = importer.load( importLine ).get();

		assertThat( filter ).hasToString( importLine );
		assertThat( importer.save( filter ) ).isEqualTo( importLine );

		assertThat( filter.getReference() ).isEqualTo( reference );
		assertThat( filter.getReferenced().toString() ).contains( fileContents );
	}

	@Test
	@ClearSystemProperty( key = "user.home" )
	void import_existing_user_filter_should_load_that_file() throws IOException {
		final Path userRoot = temp.resolve( "user" );
		System.setProperty( "user.home", userRoot.toString() );

		final String reference = "my-filter.filter";
		final String fileContents = "# This is a comment";
		final Path tmp = userRoot.resolve( Paths.get( RETEST_FOLDER_NAME, FILTER_DIR_NAME, reference ) );
		Files.createDirectories( tmp.getParent() );
		Files.write( tmp, fileContents.getBytes() );

		final String importLine = IMPORT_STATEMENT + reference;
		final ImportExternalFilterLoader importer = new ImportExternalFilterLoader();
		final ImportedExternalFilter filter = importer.load( importLine ).get();

		assertThat( filter ).hasToString( importLine );
		assertThat( importer.save( filter ) ).isEqualTo( importLine );

		assertThat( filter.getReference() ).isEqualTo( reference );
		assertThat( filter.getReferenced().toString() ).contains( fileContents );
	}

	@Test
	void import_non_existing_file_should_log_error() {
		final ImportExternalFilterLoader importer = new ImportExternalFilterLoader();
		final Optional<? extends Filter> filter = importer.load( IMPORT_STATEMENT + "nonexistent.filter" );

		assertThat( filter ).isEmpty();

		warningAndErrorLogs.assertContains( "Exception loading referenced filter 'nonexistent.filter'." );
	}
}
