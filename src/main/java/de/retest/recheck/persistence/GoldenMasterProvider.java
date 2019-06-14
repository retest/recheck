package de.retest.recheck.persistence;

import java.io.File;

import de.retest.recheck.ui.descriptors.SutState;

public interface GoldenMasterProvider {

	/**
	 * @param filePath
	 *            The file path to the Golden Master, relative to the project root.
	 * @return The Golden Master file.
	 * @throws NoGoldenMasterFoundException
	 *             If the Golden Master cannot be found.
	 */
	File getGoldenMaster( String filePath ) throws NoGoldenMasterFoundException;

	/**
	 * @param file
	 *            The Golden Master file.
	 * @return The {@code SutState} of the Golden Master.
	 */
	SutState loadGoldenMaster( File file );

	/**
	 * @param file
	 *            The Golden Master file.
	 * @param state
	 *            The {@code SutState} of the Golden Master.
	 */
	void saveGoldenMaster( File file, SutState state );

}
