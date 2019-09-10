package de.retest.recheck.persistence;

import static de.retest.recheck.Properties.RECHECK_FOLDER_NAME;
import static java.lang.String.format;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Gradle-conform file namer strategy that uses the following paths:
 * <ul>
 * <li>Golden Master files: <code>src/${SOURCE_SET_NAME}/resources/retest/recheck/</code></li>
 * <li>Result files: <code>build/test-results/${SOURCE_SET_NAME}/retest/recheck/</code></li>
 * </ul>
 * If not set, "test" will be used for <code>${SOURCE_SET_NAME}</code>.
 */
public class GradleProjectLayout extends SeparatePathsProjectLayout {

	public static final String DEFAULT_RETEST_PROJECT_PATH_FORMAT = "src/%s/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH_FORMAT = "build/test-results/%s/retest/";

	/**
	 * Creates a Gradle-conform file namer strategy using "test" for <code>${SOURCE_SET_NAME}</code>.
	 */
	public GradleProjectLayout() {
		this( "test" );
	}

	/**
	 * Creates a Gradle-conform file namer strategy using <code>sourceSetName</code> for
	 * <code>${SOURCE_SET_NAME}</code>.
	 *
	 * @param sourceSetName
	 *            value for <code>${SOURCE_SET_NAME}</code>
	 */
	public GradleProjectLayout( final String sourceSetName ) {
		super( toGoldenMasterPath( sourceSetName ), toReportPath( sourceSetName ) );
	}

	private static Path toReportPath( final String sourceSetName ) {
		return Paths.get( format( DEFAULT_RETEST_TESTREPORTS_PATH_FORMAT, validate( sourceSetName ) ),
				RECHECK_FOLDER_NAME );
	}

	private static Path toGoldenMasterPath( final String sourceSetName ) {
		return Paths.get( format( DEFAULT_RETEST_PROJECT_PATH_FORMAT, validate( sourceSetName ) ),
				RECHECK_FOLDER_NAME );
	}

	private static String validate( final String sourceSetName ) {
		Objects.requireNonNull( sourceSetName, "sourceSetName cannot be null!" );
		if ( sourceSetName.isEmpty() ) {
			throw new IllegalArgumentException( "sourceSetName cannot be empty!" );
		}
		return sourceSetName;
	}
}
