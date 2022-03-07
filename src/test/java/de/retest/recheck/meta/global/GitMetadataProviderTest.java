package de.retest.recheck.meta.global;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.GitExecutor;

class GitMetadataProviderTest {

	@Test
	void no_git_should_not_yield_metadata() {
		final GitMetadataProvider cut = new GitMetadataProvider( new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				assertThat( GitExecutor.GIT_COMMAND_VERSION.equals( command ) );

				return "'git' is not recognized as an internal or external command,\n"
						+ "operable program or batch file.\n" + "";
			}
		} );

		assertThat( cut.retrieve() ).isEmpty();
	}

	@Test
	void branch_and_commit_should_be_accurate() {
		final String branch = "feature/report-gitbranch";
		final String commit = "6e6f4dcae2fec8da1a8498d7c3b3f285b36279af";
		final GitMetadataProvider cut = new GitMetadataProvider( new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				if ( GitExecutor.GIT_COMMAND_VERSION.equals( command ) ) {
					return "git version 2.8.1";
				}
				if ( GitExecutor.GIT_COMMAND_BRANCH_NAME.equals( command ) ) {
					return branch;
				}
				if ( GitExecutor.GIT_COMMAND_COMMIT_HASH.equals( command ) ) {
					return commit;
				}
				if ( GitExecutor.GIT_COMMAND_STATUS.equals( command ) ) {
					return "On branch master";
				}
				fail( "Should not be called with " + command );
				throw new RuntimeException( "Should not be called with " + command );
			}
		} );

		final Map<String, String> metadata = cut.retrieve();
		assertThat( metadata.size() ).isEqualTo( 3 );
		assertThat( metadata.get( GitMetadataProvider.VCS_NAME ) ).isEqualTo( GitMetadataProvider.VCS_GIT );
		assertThat( metadata.get( GitMetadataProvider.BRANCH_NAME ) ).isEqualTo( branch );
		assertThat( metadata.get( GitMetadataProvider.COMMIT_NAME ) ).isEqualTo( commit );
	}

}
