package de.retest.recheck.persistence;

import java.io.File;

public interface FileNamer {

	File getFile( String extension );

	File getResultFile( String extension );

}
