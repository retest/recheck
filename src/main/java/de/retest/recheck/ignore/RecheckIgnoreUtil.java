package de.retest.recheck.ignore;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import de.retest.recheck.review.GlobalIgnoreApplier;

/**
 * @deprecated Use {@link RecheckIgnoreLocator} instead.
 */
// TODO Remove with recheck 2.0
@Deprecated
public class RecheckIgnoreUtil {

	private RecheckIgnoreUtil() {}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getProjectIgnoreFile()} instead.
	 */
	@Deprecated
	public static Optional<Path> getProjectIgnoreFile( final String filename ) {
		return new RecheckIgnoreLocator( filename ).getProjectIgnoreFile();
	}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getUserIgnoreFile()} instead.
	 */
	@Deprecated
	public static Path getUserIgnoreFile( final String filename ) {
		return new RecheckIgnoreLocator( filename ).getUserIgnoreFile();
	}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#getSuiteIgnoreFile(Path)} instead.
	 */
	@Deprecated
	public static Path getSuiteIgnoreFile( final String filename, final Path basePath ) {
		return new RecheckIgnoreLocator( filename ).getSuiteIgnoreFile( basePath );
	}

	/**
	 * @deprecated Use {@link RecheckIgnoreLocator#loadRecheckIgnore(File)} instead.
	 */
	@Deprecated
	public static GlobalIgnoreApplier loadRecheckIgnore( final File suiteIgnorePath ) {
		return RecheckIgnoreLocator.loadRecheckIgnore( suiteIgnorePath );
	}
}
