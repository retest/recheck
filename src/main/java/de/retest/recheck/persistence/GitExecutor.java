package de.retest.recheck.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GitExecutor {

	protected static final String GIT_COMMAND_VERSION = "git --version";
	protected static final String GIT_COMMAND_BRANCH_NAME = "git rev-parse --abbrev-ref HEAD";
	protected static final String GIT_COMMAND_COMMIT_HASH = "git rev-parse HEAD";

	private final boolean localGit;

	public GitExecutor() {
		localGit = isGitInstalled();
	}

	protected boolean isGitInstalled() {
		try {
			final String result = executeGitCommand( GIT_COMMAND_VERSION );
			return result != null && result.contains( "git version" );
		} catch ( final Exception e ) {
			log.debug( "`{}` resulted in exception {}, assuming no git installed.", GIT_COMMAND_VERSION,
					e.getMessage() );
			return false;
		}
	}

	protected String executeGitCommand( final String command ) throws IOException, InterruptedException {
		final Process process = Runtime.getRuntime().exec( command );
		process.waitFor();

		final BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

		return reader.readLine();
	}

	public String getCurrentBranch() {
		if ( localGit ) {
			try {
				final String result = executeGitCommand( GIT_COMMAND_BRANCH_NAME );
				if ( result.contains( " " ) ) {
					return null;
				}
				return result;
			} catch ( final Exception e ) {
				log.error( "Exception retrieving Git branch.", e );
			}
		}
		return null;
	}

	public String getCurrentCommit() {
		if ( localGit ) {
			try {
				final String result = executeGitCommand( GIT_COMMAND_COMMIT_HASH );
				if ( result.contains( " " ) ) {
					return null;
				}
				return result;
			} catch ( final Exception e ) {
				log.error( "Exception retrieving Git branch.", e );
			}
		}
		return null;
	}
}
