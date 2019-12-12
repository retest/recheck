package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class GitExecutorTest {

	@Test
	void no_git_Win10_should_return_null_without_multiple_calls() {
		final GitExecutor cut = new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				assertThat( command.equals( GitExecutor.GIT_COMMAND_VERSION ) );

				return "'git' is not recognized as an internal or external command,\n"
						+ "operable program or batch file.\n";
			}
		};

		assertThat( cut.getCurrentBranch() ).isNull();
		assertThat( cut.getCurrentCommit() ).isNull();
	}

	@Test
	void no_git_Win8_should_return_null_without_multiple_calls() {
		final GitExecutor cut = new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				assertThat( command.equals( GitExecutor.GIT_COMMAND_VERSION ) );

				return "git: The term 'git' is not recognized ...";
			}
		};

		assertThat( cut.getCurrentBranch() ).isNull();
		assertThat( cut.getCurrentCommit() ).isNull();
	}

	@Test
	void no_git_unix_should_return_null_without_multiple_calls() {
		final GitExecutor cut = new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				assertThat( command.equals( GitExecutor.GIT_COMMAND_VERSION ) );

				return "-bash: git: command not found";
			}
		};

		assertThat( cut.getCurrentBranch() ).isNull();
		assertThat( cut.getCurrentCommit() ).isNull();
	}

	@Test
	void no_repo_should_return_null_without_multiple_calls() {
		final GitExecutor cut = new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				if ( command.equals( GitExecutor.GIT_COMMAND_VERSION ) ) {
					return "git version 2.8.1";
				}
				return "fatal: Not a git repository (or any of the parent directories): .git";
			}
		};

		assertThat( cut.getCurrentBranch() ).isNull();
		assertThat( cut.getCurrentCommit() ).isNull();
	}

	@Test
	void branch_and_commit_should_be_accurate() {
		final String branch = "feature/report-gitbranch";
		final String commit = "6e6f4dcae2fec8da1a8498d7c3b3f285b36279af";
		final GitExecutor cut = new GitExecutor() {
			@Override
			protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
				if ( command.equals( GitExecutor.GIT_COMMAND_VERSION ) ) {
					return "git version 2.8.1";
				}
				if ( command.equals( GitExecutor.GIT_COMMAND_BRANCH_NAME ) ) {
					return branch;
				}
				if ( command.equals( GitExecutor.GIT_COMMAND_COMMIT_HASH ) ) {
					return commit;
				}
				if ( command.equals( GitExecutor.GIT_COMMAND_STATUS ) ) {
					return "On branch master";
				}
				fail( "Should not be called with " + command );
				throw new RuntimeException( "Should not be called with " + command );
			}
		};

		assertThat( cut.getCurrentBranch() ).isEqualTo( branch );
		assertThat( cut.getCurrentCommit() ).isEqualTo( commit );
	}

}
