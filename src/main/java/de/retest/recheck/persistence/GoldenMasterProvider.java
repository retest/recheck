package de.retest.recheck.persistence;

import java.io.File;

import de.retest.recheck.ui.descriptors.SutState;

public interface GoldenMasterProvider {

	File getGoldenMaster( String fileName ) throws NoGoldenMasterFoundException;

	SutState loadGoldenMaster( File file );

	void saveGoldenMaster( File file, SutState state );

}
