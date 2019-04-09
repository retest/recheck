package de.retest.recheck;

import static de.retest.recheck.Properties.RECHECK_FOLDER_NAME;

import java.io.File;

import de.retest.recheck.persistence.FileNamer;

/**
 * Maven-conform file namer strategy that uses the following paths:
 * <ul>
 * <li>Golden Master files: <code>src/test/resources/retest/recheck/</code></li>
 * <li>Result files: <code>target/test-classes/retest/recheck/</code></li>
 * </ul>
 */
public class MavenConformFileNamerStrategy implements FileNamerStrategy {

	public static final String DEFAULT_RETEST_PROJECT_PATH = "src/test/resources/retest/";
	public static final String DEFAULT_RETEST_TESTREPORTS_PATH = "target/test-classes/retest/";

	@Override
	public FileNamer createFileNamer( final String... baseNames ) {
		return new FileNamer() {
			@Override
			public File getFile( final String extension ) {
				return toFile( DEFAULT_RETEST_PROJECT_PATH, extension, baseNames );
			}

			@Override
			public File getResultFile( final String extension ) {
				return toFile( DEFAULT_RETEST_TESTREPORTS_PATH, extension, baseNames );
			}
		};
	}

	private File toFile( final String prefix, final String extension, final String... baseNames ) {
		final String baseName = String.join( File.separator, baseNames );
		return new File( prefix + File.separator + RECHECK_FOLDER_NAME + File.separator + baseName + extension );
	}

}
