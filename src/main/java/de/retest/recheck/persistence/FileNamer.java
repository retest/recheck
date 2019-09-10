package de.retest.recheck.persistence;

import java.io.File;

/**
 * @deprecated Use {@link ProjectLayout} instead.
 */
@Deprecated
public interface FileNamer {

	/**
	 * @param extension
	 *            The file extension to be used.
	 * @return The Golden Master file.
	 */
	File getFile( String extension );

	/**
	 * @param extension
	 *            The file extension to be used.
	 * @return The test report file.
	 */
	File getResultFile( String extension );

}
