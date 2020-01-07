package de.retest.recheck.review.workers;

import static de.retest.recheck.RecheckProperties.RETEST_FOLDER_NAME;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;

import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader;
import de.retest.recheck.review.ignore.io.ErrorHandlingLoader;
import io.github.netmikey.logunit.api.LogCapturer;

class MigrateIgnoreFileTest {

	@RegisterExtension
	LogCapturer warningAndErrorLogs = LogCapturer.create() //
			.captureForType( AttributeFilterLoader.class ) //
			.captureForType( FilterPreserveLineLoader.class ) //
			.captureForType( ErrorHandlingLoader.class );

	Path configFolder;
	Path migratedIgnoreFile;
	Path tempIgnoreFile;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws Exception {
		configFolder = temp.resolve( RETEST_FOLDER_NAME );
		Files.createDirectory( configFolder );

		final Path jsIgnoreFile = configFolder.resolve( RECHECK_IGNORE_JSRULES );
		Files.createFile( jsIgnoreFile );

		final Path origIgnoreFile =
				Paths.get( "src/test/resources/de/retest/recheck/review/workers/legacy-recheck.ignore" );
		migratedIgnoreFile = Paths.get( "src/test/resources/de/retest/recheck/review/workers/" + RECHECK_IGNORE );
		tempIgnoreFile = configFolder.resolve( RECHECK_IGNORE );
		Files.copy( origIgnoreFile, tempIgnoreFile );

	}

	@Test
	@ClearSystemProperty( key = RETEST_PROJECT_ROOT )
	void loaded_ignore_file_should_be_migrated() throws Exception {
		System.setProperty( RETEST_PROJECT_ROOT, configFolder.toString() );
		final LoadFilterWorker load = new LoadFilterWorker( NopCounter.getInstance() );
		final GlobalIgnoreApplier gia = load.load();
		final SaveFilterWorker save = new SaveFilterWorker( gia );

		save.save();

		assertThat( tempIgnoreFile ).hasSameContentAs( migratedIgnoreFile );

		assertThat( warningAndErrorLogs.size() ).isEqualTo( 3 );
		warningAndErrorLogs.assertContains(
				"'attribute=possible-*regex' contains '*'. For regular expressions, please use 'attribute-regex=possible-*regex'." );
		warningAndErrorLogs.assertContains( "Please remove leading whitespace from the following line:\n" //
				+ " attribute:foo" );
		warningAndErrorLogs.assertContains(
				"For ignoring an attribute globally, please use 'attribute=' (to ensure weired line breaks do not break your ignore file)." );
	}

}
