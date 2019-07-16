package de.retest.recheck.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitUtil {

	private static final String GIT_COMMAND_BRANCH_NAME = "git rev-parse --abbrev-ref HEAD";
	private static final String GIT_COMMAND_COMMIT_HASH = "git rev-parse HEAD";

	private static final Logger logger = LoggerFactory.getLogger( GitUtil.class );

	protected GitUtil() {}

	private static String executeGitCommand( final String command ) throws IOException, InterruptedException {
		final Process process = Runtime.getRuntime().exec( command );
		process.waitFor();

		final BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

		return reader.readLine();
	}

	public static String getCurrentGitBranch() {
		try {
			return executeGitCommand( GIT_COMMAND_BRANCH_NAME );
		} catch ( final Exception e ) {
			logger.error( "Exception retrieving Git branch.", e );
			return null;
		}
	}

	public static String getCurrentGitCommit() {
		try {
			return executeGitCommand( GIT_COMMAND_COMMIT_HASH );
		} catch ( final Exception e ) {
			logger.error( "Exception retrieving Git branch.", e );
			return null;
		}
	}

	public static void main( final String[] args ) {
		System.out.println( "Git branch: " + getCurrentGitBranch() + ", commit: " + getCurrentGitCommit() );
	}
}
