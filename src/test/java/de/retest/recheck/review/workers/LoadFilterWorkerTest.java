package de.retest.recheck.review.workers;

import static de.retest.recheck.Properties.RETEST_FOLDER_NAME;
import static de.retest.recheck.configuration.ProjectConfiguration.RECHECK_IGNORE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

public class LoadFilterWorkerTest {

	@Test
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void loading_without_ignore_file_should_fail( @TempDir final Path root ) throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "/root/doesn't/exist" );
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root );
		assertThat( worker.load() ).isNull();
	}

	@Test
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void loading_with_project_ignore_file_should_succeed( @TempDir final Path root ) throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, root.toString() );
		final Path directory =
				java.nio.file.Files.createDirectories( Paths.get( root.toString(), RETEST_FOLDER_NAME ) );
		final Path ignoreFile = java.nio.file.Files.createFile( Paths.get( directory.toString(), RECHECK_IGNORE ) );
		givenFileWithLines( ignoreFile.toFile(), "#" );
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root );
		assertThat( worker.load() ).isNotNull();
	}

	@Test
	@SystemProperty( key = "user.home" )
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void loading_with_user_ignore_file_should_succeed( @TempDir final Path root ) throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "/root/doesn't/exist" );
		System.setProperty( "user.home", root.toAbsolutePath().toString() );
		final Path directory = java.nio.file.Files
				.createDirectories( Paths.get( System.getProperty( "user.home" ), RETEST_FOLDER_NAME ) );
		final Path ignoreFile = java.nio.file.Files.createFile( Paths.get( directory.toString(), RECHECK_IGNORE ) );
		givenFileWithLines( ignoreFile.toFile(), "#" );
		final LoadFilterWorker worker = new LoadFilterWorker( NopCounter.getInstance(), root );
		assertThat( worker.load() ).isNotNull();
	}

	@Test
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void loading_with_suite_ignore_file_should_succeed( @TempDir final Path root ) throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, "/root/doesn't/exist" );
		final Path directory = java.nio.file.Files.createDirectories( Paths.get( root.toString(), "/src" ) );
		final Path ignoreFile = java.nio.file.Files.createFile( Paths.get( directory.toString(), RECHECK_IGNORE ) );
		givenFileWithLines( ignoreFile.toFile(), "#" );
		final LoadFilterWorker worker =
				new LoadFilterWorker( NopCounter.getInstance(), Paths.get( root.toString(), "/src" ) );
		assertThat( worker.load() ).isNotNull();
	}

	@Test
	@SystemProperty( key = "user.home" )
	@SystemProperty( key = ProjectConfiguration.RETEST_PROJECT_ROOT )
	void loading_with_all_ignore_files_should_contain_all_lines( @TempDir final Path root ) throws Exception {
		System.setProperty( ProjectConfiguration.RETEST_PROJECT_ROOT, root.toString() );
		System.setProperty( "user.home", root.toAbsolutePath().toString() );
		final Path projectAndUserDirectory =
				java.nio.file.Files.createDirectories( Paths.get( root.toString(), RETEST_FOLDER_NAME ) );
		final Path projectAndUserIgnoreFile =
				java.nio.file.Files.createFile( Paths.get( projectAndUserDirectory.toString(), RECHECK_IGNORE ) );
		final Path suiteDirectory = java.nio.file.Files.createDirectories( Paths.get( root.toString(), "/src" ) );
		final Path suiteIgnoreFile =
				java.nio.file.Files.createFile( Paths.get( suiteDirectory.toString(), RECHECK_IGNORE ) );
		givenFileWithLines( projectAndUserIgnoreFile.toFile(), "#" );
		givenFileWithLines( suiteIgnoreFile.toFile(), "#" );
		final LoadFilterWorker worker =
				new LoadFilterWorker( NopCounter.getInstance(), Paths.get( root.toString(), "/src" ) );
		assertThat( worker.load().persist().getIgnores().toString() ).contains( "#, #, #" );
	}

	private static void givenFileWithLines( final File file, final String lines ) throws IOException {
		Files.asCharSink( file, Charsets.UTF_8 ).write( lines );
	}
}
