package de.retest.recheck.review.workers;

import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.retest.recheck.review.counter.NopCounter;

public class LoadFilterWorkerTest {

	@Test
	void loading_without_ignore_file_should_fail( @TempDir final Path root ) throws Exception {
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root.toFile() );
		assertThatThrownBy( worker::load ) //
				.isExactlyInstanceOf( NoSuchFileException.class ) //
				.hasMessage( "No '" + RECHECK_IGNORE + "' found." );
	}

	@Test
	void loading_with_ignore_file_should_succeed( @TempDir final Path root ) throws Exception {
		givenFileWithLines( root.resolve( RECHECK_IGNORE ).toFile(), "#" );
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root.toFile() );
		assertThat( worker.load() ).isNotNull();
	}

	private static void givenFileWithLines( final File file, final String lines ) throws IOException {
		Files.asCharSink( file, Charsets.UTF_8 ).write( lines );
	}
}
