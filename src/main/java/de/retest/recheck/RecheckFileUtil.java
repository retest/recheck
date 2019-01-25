package de.retest.recheck;

import java.io.File;
import java.io.FilenameFilter;

import de.retest.util.FileUtil;

public class RecheckFileUtil {

	// TODO Replace with values from recheck project.
	public static final String RECHECK_FILE_EXTENSION = ".recheck";
	public static final String RECHECK_FOLDER_NAME = "recheck";

	public static File getRecheckFolder() {
		return FileUtil.ensureFolderInWorkDir( "recheck", "N/A", RECHECK_FOLDER_NAME );
	}

	public static final FilenameFilter FILE_FILTER = ( dir, name ) -> name.endsWith( RECHECK_FILE_EXTENSION );

}
