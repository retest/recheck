package de.retest.recheck.persistence;

import java.io.File;

import de.retest.recheck.ui.descriptors.SutState;

public interface GoldenMasterProvider {

	File getRecheckStateFile( String fileName ) throws NoGoldenMasterFoundException;

	SutState loadRecheckState( File file );

	void saveRecheckState( File file, SutState state );

}
