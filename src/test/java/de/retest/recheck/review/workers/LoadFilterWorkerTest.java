package de.retest.recheck.review.workers;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.review.counter.NopCounter;

public class LoadFilterWorkerTest {

	@Test
	void loading_without_ignore_file_should_fail( @TempDir final Path root ) throws Exception {
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root.toFile() );
		assertThrows( IllegalArgumentException.class, () -> worker.load() );
	}

	@Test
	void loading_with_ignore_file_should_succeed( @TempDir final Path root ) throws Exception {

		givenFileWithLines( root.resolve( ProjectConfiguration.RECHECK_IGNORE ).toFile(), "#" );

		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root.toFile() );
		assertNotNull( worker.load() );
	}

	private static void givenFileWithLines( final File file, final String lines ) throws IOException {
		Files.asCharSink( file, Charsets.UTF_8 ).write( lines );
	}
}
