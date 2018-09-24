package de.retest.recheck;

import de.retest.persistence.FileNamer;

public interface FileNamerStrategy {

	public static final String RECHECK_FILE_EXTENSION = "recheck";
	public static final String RECHECK_FOLDER_PROPERTY = "de.retest.RecheckFolder";
	public static final String RECHECK_FOLDER_DEFAULT = "recheck";

	public FileNamer createFileNamer( String... baseNames );

	public String getTestClassName();

	String getTestMethodName();
}
