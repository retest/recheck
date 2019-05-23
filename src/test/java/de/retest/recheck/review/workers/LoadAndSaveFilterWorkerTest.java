package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

class LoadAndSaveFilterWorkerTest {

	Path origIgnoreFile;
	Path tempIgnoreFile;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws Exception {
		final Path configFolder = temp.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		Files.createDirectory( configFolder );

		final Path jsIgnoreFile = configFolder.resolve( RECHECK_IGNORE_JSRULES );
		Files.createFile( jsIgnoreFile );

		origIgnoreFile = Paths.get( "src/test/resources/de/retest/recheck/review/workers/" + RECHECK_IGNORE );
		tempIgnoreFile = configFolder.resolve( RECHECK_IGNORE );
		Files.copy( origIgnoreFile, tempIgnoreFile );

		System.setProperty( RETEST_PROJECT_ROOT, configFolder.toString() );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void loaded_ignore_file_should_be_saved_without_changes() throws Exception {
		final LoadFilterWorker load = new LoadFilterWorker( NopCounter.getInstance() );
		final GlobalIgnoreApplier gia = load.load();
		final SaveFilterWorker save = new SaveFilterWorker( gia );
		save.save();
		assertThat( tempIgnoreFile ).hasSameContentAs( origIgnoreFile );
	}

}
