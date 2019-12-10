package de.retest.recheck.meta.global;

import java.util.HashMap;
import java.util.Map;

import de.retest.recheck.meta.MetadataProvider;
import de.retest.recheck.persistence.GitExecutor;

public final class GitMetadataProvider implements MetadataProvider {

	public static final String VCS_GIT = "git";
	public static final String VCS_NAME = "vcs.name";
	public static final String BRANCH_NAME = "vcs.branch";
	public static final String COMMIT_NAME = "vcs.commit";

	private final GitExecutor git;

	public GitMetadataProvider() {
		this( new GitExecutor() );
	}

	protected GitMetadataProvider( final GitExecutor git ) {
		this.git = git;
	}

	@Override
	public Map<String, String> retrieve() {
		final Map<String, String> map = new HashMap<>();

		final String branch = git.getCurrentBranch();
		final String commit = git.getCurrentCommit();
		if ( branch != null && commit != null ) {
			map.put( VCS_NAME, VCS_GIT );
			map.put( BRANCH_NAME, branch );
			map.put( COMMIT_NAME, commit );
		}

		return map;
	}
}
