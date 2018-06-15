package de.retest.recheck;

import de.retest.persistence.FileNamer;

public interface FileNamerStrategy {

	public FileNamer createFileNamer( String... baseNames );

	public String getTestClassName();

	String getTestMethodName();
}
