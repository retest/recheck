package de.retest.recheck;

import static de.retest.recheck.Properties.RECHECK_FOLDER_NAME;

import java.io.File;
import java.util.Objects;

import de.retest.recheck.persistence.FileNamer;

public class GradleConformFileNamerStrategy implements FileNamerStrategy {
	private final String sourceSetName;

	public GradleConformFileNamerStrategy() {
		this( "test" );
	}

	public GradleConformFileNamerStrategy( final String sourceSetName ) {
		Objects.requireNonNull( sourceSetName, "sourceSetName cannot be null!" );
		if ( sourceSetName.isEmpty() ) {
			throw new IllegalArgumentException( "sourceSetName cannot be empty!" );
		}
		this.sourceSetName = sourceSetName;
	}

	public static final String DEFAULT_RETEST_WORKSPACE_PATH = "src/%s/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH = "build/test-results/%s/retest/";

	@Override
	public FileNamer createFileNamer( final String... baseNames ) {
		return new FileNamer() {
			@Override
			public File getFile( final String extension ) {
				return toFile( DEFAULT_RETEST_WORKSPACE_PATH, extension, baseNames );
			}

			@Override
			public File getResultFile( final String extension ) {
				return toFile( DEFAULT_RETEST_TESTREPORTS_PATH, extension, baseNames );
			}
		};
	}

	private File toFile( final String prefixFormat, final String extension, final String... baseNames ) {
		final String baseName = String.join( File.separator, baseNames );
		final String prefix = String.format( prefixFormat, sourceSetName );
		return new File( prefix + File.separator + RECHECK_FOLDER_NAME + File.separator + baseName + extension );
	}

}
