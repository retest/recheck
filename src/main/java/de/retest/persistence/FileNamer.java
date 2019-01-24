package de.retest.persistence;

import java.io.File;

public interface FileNamer {

	public File getFile( String extension );

	public File getResultFile( String extension );

}
