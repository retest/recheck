package de.retest.recheck.persistence;

import static de.retest.recheck.RecheckProperties.RECHECK_FOLDER_NAME;

import java.nio.file.Paths;

/**
 * Maven-conform file namer that uses the following paths:
 * <ul>
 * <li>Golden Master files: <code>src/test/resources/retest/recheck/</code></li>
 * <li>Result files: <code>target/test-classes/retest/recheck/</code></li>
 * </ul>
 */
public class MavenProjectLayout extends SeparatePathsProjectLayout {

	public static final String DEFAULT_RETEST_PROJECT_PATH = "src/test/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH = "target/test-classes/retest/";

	public MavenProjectLayout() {
		super( Paths.get( DEFAULT_RETEST_PROJECT_PATH, RECHECK_FOLDER_NAME ),
				Paths.get( DEFAULT_RETEST_TESTREPORTS_PATH, RECHECK_FOLDER_NAME ) );
	}

}
