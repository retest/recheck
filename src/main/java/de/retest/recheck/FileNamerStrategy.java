package de.retest.recheck;

import de.retest.persistence.FileNamer;

public interface FileNamerStrategy {

	public static final String RECHECK_FILE_EXTENSION = ".recheck";
	public static final String RECHECK_FOLDER_NAME = "recheck";

	public FileNamer createFileNamer( String... baseNames );

	public String getTestClassName();

	String getTestMethodName();
}
