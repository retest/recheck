package de.retest.recheck;

import java.io.File;

import de.retest.recheck.ui.descriptors.SutState;

public interface SutStateLoader {

	SutState loadExpected( final File file );

	SutState createNew( final File file, final SutState actual );
}
