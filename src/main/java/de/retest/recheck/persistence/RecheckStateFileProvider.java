package de.retest.recheck.persistence;

import java.io.File;

import de.retest.recheck.ui.descriptors.SutState;

public interface RecheckStateFileProvider {

	File getRecheckStateFile( String fileName ) throws NoStateFileFoundException;

	SutState loadRecheckState( File file );

	void saveRecheckState( File file, SutState state );

}
