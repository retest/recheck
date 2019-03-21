package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE_JSRULES;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_ROOT;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

class LoadShouldIgnoreWorkerTest {

	Path defaultIgnoreFile;

	@BeforeEach
	void setUp( @TempDir final Path temp ) throws Exception {
		final Path configFolder = temp.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		Files.createDirectory( configFolder );

		final Path jsIgnoreFile = configFolder.resolve( RECHECK_IGNORE_JSRULES );
		Files.createFile( jsIgnoreFile );

		defaultIgnoreFile = configFolder.resolve( RECHECK_IGNORE );
		Files.copy( Paths.get( "src/main/resources/default-" + RECHECK_IGNORE ), defaultIgnoreFile );

		System.setProperty( RETEST_PROJECT_ROOT, configFolder.toString() );
	}

	@Test
	@SystemProperty( key = RETEST_PROJECT_ROOT )
	void loaded_ignore_file_should_match_default_ignore_file() throws Exception {
		final LoadShouldIgnoreWorker cut = new LoadShouldIgnoreWorker( NopCounter.getInstance() );
		final GlobalIgnoreApplier globalIgnoreApplier = cut.load();
		final List<String> loadedIgnoreFileLines = globalIgnoreApplier.persist().getIgnores().stream() //
				.map( Object::toString ) //
				.collect( Collectors.toList() );

		final List<String> defaultIgnoreFileLines = Files.readAllLines( defaultIgnoreFile );
		assertThat( loadedIgnoreFileLines ).isEqualTo( defaultIgnoreFileLines );
	}

}
