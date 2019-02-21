package de.retest.recheck.util;

import java.io.File;
import java.io.IOException;

public class GetRelativeFilePath {

	public static final String TEST_FILE_SEPARATOR = "/";

	public static String getRelativeFilePath( final String basePath, final File file ) throws IOException {
		return getRelativeFilePath( file.getCanonicalFile(), new File( basePath ) );
	}

	// TODO In Java 8 use Path.relativize(Path) but for now we use:
	// https://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls (01.06.2017)
	public static String getRelativeFilePath( final File baseFile, final File file ) throws IOException {
		final String bpath = baseFile.getCanonicalPath();
		final String fpath = file.getCanonicalPath();

		if ( fpath.startsWith( bpath ) ) {
			final int length = bpath.length();
			if ( length == fpath.length() ) {
				return ".";
			}
			return fpath.substring( length + 1 );
		}

		final File parent = baseFile.getParentFile();
		if ( parent == null ) {
			throw new IOException( "No common directory" );
		}
		return FileUtil.canonicalPathQuietly( file );
	}

	public static File fromRelativePath( final File rootDir, final String file ) {
		return new File( rootDir, file );
	}

	public static String getRelativeFilePathSilently( final File baseFile, final File file ) {
		try {
			return getRelativeFilePath( baseFile, file );
		} catch ( final IOException e ) {
			// mute
		}
		return file.getPath();
	}
}
