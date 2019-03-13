package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import de.retest.recheck.report.SuiteReplayResult;

@ExtendWith( TempDirectory.class )
class RecheckTestReportUtilTest {

	File file;
	File fileMissingFolders;

	@BeforeEach
	void setUp( @TempDir final Path temp ) {
		file = temp.resolve( "test.report" ).toFile();
		fileMissingFolders = temp.resolve( "retest" + File.separator + "recheck" + File.separator + "test.report" ).toFile();
	}

	@Test
	void persist_should_create_report_if_no_errors() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 0 );

		RecheckTestReportUtil.persist( replayResult, file );
		assertThat( file.exists() ).isTrue();
	}

	@Test
	void persist_should_create_report_if_errors() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 1 );

		RecheckTestReportUtil.persist( replayResult, file );
		assertThat( file.exists() ).isTrue();
	}

	@Test
	void persist_should_create_missing_folders() {
		final SuiteReplayResult replayResult = mock( SuiteReplayResult.class );
		when( replayResult.getDifferencesCount() ).thenReturn( 0 );

		RecheckTestReportUtil.persist( replayResult, fileMissingFolders );
	}
}
