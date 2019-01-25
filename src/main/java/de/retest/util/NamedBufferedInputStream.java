package de.retest.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NamedBufferedInputStream extends BufferedInputStream {

	private final String name;

	public NamedBufferedInputStream( final InputStream in, final String name ) {
		super( in );
		this.name = name;
	}

	public NamedBufferedInputStream( final File file ) throws FileNotFoundException {
		super( new FileInputStream( file ) );
		name = file.getAbsolutePath();
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + name + "]";
	}
}
