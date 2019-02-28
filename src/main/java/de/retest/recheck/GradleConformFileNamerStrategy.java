package de.retest.recheck;

import java.io.File;

import de.retest.recheck.persistence.FileNamer;

public class GradleConformFileNamerStrategy implements FileNamerStrategy {

	public static final String DEFAULT_RETEST_WORKSPACE_PATH = "src/test/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH = "build/test-results/test/";

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

	private File toFile( final String prefix, final String extension, final String... baseNames ) {
		final String baseName = String.join( File.separator, baseNames );
		return new File( prefix + File.separator + baseName + extension );
	}

}
