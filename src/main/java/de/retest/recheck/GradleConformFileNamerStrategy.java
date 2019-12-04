package de.retest.recheck;

import static de.retest.recheck.RecheckProperties.RECHECK_FOLDER_NAME;

import java.io.File;
import java.util.Objects;

import de.retest.recheck.persistence.FileNamer;

/**
 * Gradle-conform file namer strategy that uses the following paths:
 * <ul>
 * <li>Golden Master files: <code>src/${SOURCE_SET_NAME}/resources/retest/recheck/</code></li>
 * <li>Result files: <code>build/test-results/${SOURCE_SET_NAME}/retest/recheck/</code></li>
 * </ul>
 * If not set, "test" will be used for <code>${SOURCE_SET_NAME}</code>.
 */
public class GradleConformFileNamerStrategy implements FileNamerStrategy {
	private final String sourceSetName;

	/**
	 * Creates a Gradle-conform file namer strategy using "test" for <code>${SOURCE_SET_NAME}</code>.
	 */
	public GradleConformFileNamerStrategy() {
		this( "test" );
	}

	/**
	 * Creates a Gradle-conform file namer strategy using <code>sourceSetName</code> for
	 * <code>${SOURCE_SET_NAME}</code>.
	 *
	 * @param sourceSetName
	 *            value for <code>${SOURCE_SET_NAME}</code>
	 */
	public GradleConformFileNamerStrategy( final String sourceSetName ) {
		Objects.requireNonNull( sourceSetName, "sourceSetName cannot be null!" );
		if ( sourceSetName.isEmpty() ) {
			throw new IllegalArgumentException( "sourceSetName cannot be empty!" );
		}
		this.sourceSetName = sourceSetName;
	}

	public static final String DEFAULT_RETEST_PROJECT_PATH_FORMAT = "src/%s/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH_FORMAT = "build/test-results/%s/retest/";

	@Override
	public FileNamer createFileNamer( final String... baseNames ) {
		return new FileNamer() {
			@Override
			public File getFile( final String extension ) {
				return toFile( DEFAULT_RETEST_PROJECT_PATH_FORMAT, extension, baseNames );
			}

			@Override
			public File getResultFile( final String extension ) {
				return toFile( DEFAULT_RETEST_TESTREPORTS_PATH_FORMAT, extension, baseNames );
			}
		};
	}

	private File toFile( final String prefixFormat, final String extension, final String... baseNames ) {
		final String baseName = String.join( File.separator, baseNames );
		final String prefix = String.format( prefixFormat, sourceSetName );
		return new File( prefix + File.separator + RECHECK_FOLDER_NAME + File.separator + baseName + extension );
	}

}
