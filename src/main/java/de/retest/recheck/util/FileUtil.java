package de.retest.recheck.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import de.retest.recheck.ioerror.ReTestLoadException;
import de.retest.recheck.ioerror.ReTestSaveException;

public class FileUtil {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( FileUtil.class );

	public static final FileFilter PNG_FILTER = file -> file.getName().endsWith( ".png" );

	public static String readFileToString( final File source ) {
		if ( source == null ) {
			return "";
		}
		try {
			return FileUtils.readFileToString( source, StandardCharsets.UTF_8 );
		} catch ( final IOException exc ) {
			throw new RuntimeException( exc );
		}
	}

	public static String canonicalPathQuietly( final File file ) {
		try {
			if ( file != null ) {
				return file.getCanonicalPath();
			} else {
				return "null";
			}
		} catch ( final IOException exc ) {
			final String result = file.getAbsolutePath();
			logger.error( "Exception getting canonical path for file {}.", result, exc );
			return result;
		}
	}

	public static File canonicalFileQuietly( final File file ) {
		try {
			if ( file != null ) {
				return file.getCanonicalFile();
			}
		} catch ( final IOException exc ) {
			logger.error( "Exception getting canonical file for file {}.", file.getPath(), exc );
		}
		return file;
	}

	public static void deleteRecursively( final File file ) {
		FileUtils.deleteQuietly( file );
	}

	public static void copy( final File from, final File to ) throws IOException {
		if ( from.isDirectory() ) {
			FileUtils.copyDirectory( from, to, true );
			return;
		}
		FileUtils.copyFile( from, to, true );
	}

	public static List<File> listFilesRecursively( final File folder, final FileFilter filter ) {
		if ( !folder.isDirectory() ) {
			throw new IllegalArgumentException( "'" + folder + "' is not a directory!" );
		}
		final List<File> result = new ArrayList<>();
		final File[] files = folder.listFiles();
		if ( files == null ) {
			return result;
		}
		for ( final File file : files ) {
			if ( filter.accept( file ) ) {
				result.add( file );
			}
			if ( file.isDirectory() ) {
				result.addAll( listFilesRecursively( file, filter ) );
			}
		}
		return result;
	}

	public static List<File> listFilesRecursively( final File folder, final FilenameFilter filter ) {
		if ( !folder.isDirectory() ) {
			throw new IllegalArgumentException( "'" + folder + "' is not a directory!" );
		}
		final List<File> result = new ArrayList<>();
		final File[] files = folder.listFiles();
		if ( files == null ) {
			return result;
		}
		for ( final File file : files ) {
			if ( filter.accept( file, file.getName() ) ) {
				result.add( file );
			}
			if ( file.isDirectory() ) {
				result.addAll( listFilesRecursively( file, filter ) );
			}
		}
		return result;
	}

	public static List<File> listFilesRecursively( final File folder,
			final javax.swing.filechooser.FileFilter filter ) {
		if ( !folder.isDirectory() ) {
			throw new IllegalArgumentException( "'" + folder + "' is not a directory!" );
		}
		final List<File> result = new ArrayList<>();
		final File[] files = folder.listFiles();
		if ( files == null ) {
			return result;
		}
		for ( final File file : files ) {
			if ( filter.accept( file ) ) {
				result.add( file );
			}
			if ( file.isDirectory() ) {
				result.addAll( listFilesRecursively( file, filter ) );
			}
		}
		return result;
	}

	public static String normalize( final String text ) {
		return text.replaceAll( "[%\\.\"\\*/:<>\\?\\\\\\|\\+,\\.;=\\[\\]]", "_" ).trim();
	}

	public static void ensureFolder( final File result ) throws ReTestSaveException {
		if ( result.getParent() != null ) {
			new File( result.getParent() ).mkdirs();
			return;
		}
		try {
			result.getCanonicalFile().getParentFile().mkdirs();
		} catch ( final IOException exc ) {
			logger.error( "Exception creating parent folder of file {}.", result, exc );
			throw new ReTestSaveException( result, "Exception creating parent folder.", exc );
		}
	}

	public static List<File> convertNames2Files( final File baseDir, final String[] fileNames ) {
		final List<File> result = new ArrayList<>();
		for ( final String fileName : fileNames ) {
			result.add( new File( baseDir, fileName ) );
		}
		return result;
	}

	public static List<File> convertSemicolonSeparatedString2ListOfFiles( final File baseDir, final String fileList )
			throws IOException {
		final List<File> result = new ArrayList<>();
		if ( fileList == null || fileList.trim().isEmpty() ) {
			return result;
		}
		final String[] files = fileList.split( ";" );
		for ( final String fileName : files ) {
			if ( fileName.trim().isEmpty() ) {
				continue;
			}
			final File file = new File( baseDir, fileName );
			if ( !file.exists() ) {
				throw new IOException( "File '" + canonicalPathQuietly( file ) + "' does not exist!" );
			}
			result.add( file );
		}
		return result;
	}

	public static String convertListOfFiles2SemicolonJoinedString( final File baseDir, final List<File> files )
			throws IOException {
		String result = "";
		if ( files == null || files.isEmpty() ) {
			return result;
		}
		for ( final File file : files ) {
			result += GetRelativeFilePath.getRelativeFilePath( baseDir, file ) + ";";
		}
		return result.substring( 0, result.length() - 1 );
	}

	public static URL toUrl( final File file ) throws IOException {
		try {
			return new URL( "file:" + file.getCanonicalPath() );
		} catch ( final MalformedURLException exc ) {
			throw new RuntimeException( exc );
		}
	}

	public static interface Writer {
		void write( FileOutputStream out ) throws IOException;
	}

	public static void writeToFile( final File file, final Writer writer ) throws IOException {
		FileOutputStream out = null;
		try {
			ensureFolder( file );
			out = new FileOutputStream( file );
			writer.write( out );
		} catch ( final Exception e ) {
			throw new ReTestSaveException( file, e );
		} finally {
			try {
				if ( out != null ) {
					out.close();
				}
			} catch ( final IOException e ) {
				// NOP.
			}
		}
	}

	public static boolean tryWriteToFile( final File file, final Writer writer ) {
		FileOutputStream out = null;
		try {
			file.getParentFile().mkdirs();
			out = new FileOutputStream( file );
			writer.write( out );
			return true;
		} catch ( final Exception e ) {
			logger.error( "Error writing to file '{}', ignoring: {}", file, e.getMessage() );
			return false;
		} finally {
			try {
				if ( out != null ) {
					out.close();
				}
			} catch ( final IOException e ) {
				// NOP.
			}
		}
	}

	public static interface Reader<T> {
		T read( NamedBufferedInputStream in ) throws IOException;
	}

	public static <T> T readFromFile( final File file, final Reader<T> reader ) throws IOException {
		try ( final NamedBufferedInputStream in =
				new NamedBufferedInputStream( new FileInputStream( file ), file.getName() ) ) {
			return reader.read( in );
		} catch ( final Exception e ) {
			throw new ReTestLoadException( file.toURI(), e );
		}
	}

	public static <T> T tryReadFromFile( final File file, final Reader<T> reader ) {
		try ( final NamedBufferedInputStream in =
				new NamedBufferedInputStream( new FileInputStream( file ), file.getName() ) ) {
			return reader.read( in );
		} catch ( final IOException e ) {
			logger.warn( "Error reading from file '{}', ignoring: {}", canonicalPathQuietly( file ), e.getMessage() );
			return null;
		}
	}

	public static interface ZipReader<T> {
		T read( ZipFile in ) throws IOException;
	}

	public static <T> T readFromZipFile( final File file, final ZipReader<T> reader ) throws IOException {
		ZipFile in = null;
		try {
			in = new ZipFile( file );
			return reader.read( in );
		} catch ( final Exception e ) {
			throw new ReTestLoadException( file.toURI(), e );
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( final IOException e ) {
					logger.error( "Exception closing input stream for zip file {}.", file, e );
				}
			}
		}
	}

	public static <T> T tryReadFromZipFile( final File file, final ZipReader<T> reader ) {
		if ( !file.exists() ) {
			logger.error( "File '{}' does not exist!", canonicalPathQuietly( file ) );
			return null;
		}
		ZipFile in = null;
		try {
			in = new ZipFile( file );
			return reader.read( in );
		} catch ( final IllegalArgumentException exc ) {
			logger.warn( "Error reading from file with wrong XML-version: {}", canonicalPathQuietly( file ), exc );
			throw exc;
		} catch ( final Exception e ) {
			logger.warn( "Error reading from file '{}', ignoring: ", canonicalPathQuietly( file ), e );
			return null;
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( final IOException e ) {
					logger.error( "Exception closing input stream for file {}.", file, e );
				}
			}
		}
	}

	public static String removeExtension( final String fileName ) {
		if ( fileName.lastIndexOf( '.' ) == -1 ) {
			return fileName;
		}
		return fileName.substring( 0, fileName.lastIndexOf( '.' ) );
	}

	public static String removeRelativePathExtension( final File rootDir, final File file ) {
		return removeExtension( GetRelativeFilePath.getRelativeFilePathSilently( rootDir, file ) );
	}

	public static double getFileSizeInMB( final File report ) {
		try ( final InputStream stream = new FileInputStream( report ) ) {
			return stream.available() / 1024.0 / 1024.0;
		} catch ( final IOException e ) {
			logger.warn( "Exception getting file length of file '{}'.", canonicalPathQuietly( report ), e );
		}
		return -1;
	}

	public static NamedBufferedInputStream getInputStreamFrom( final File baseDir, final String input )
			throws IOException {
		try {
			// TODO Convert URL using encoding
			final URL url = new URL( input );
			return new NamedBufferedInputStream( url.openStream(), input );
		} catch ( final IOException exc ) {}
		File result = null;
		// relative path
		result = new File( baseDir, input ).getCanonicalFile();
		if ( result.exists() ) {
			return new NamedBufferedInputStream( new FileInputStream( result ), input );
		}
		// absolute path
		result = new File( input );
		if ( result.exists() ) {
			return new NamedBufferedInputStream( new FileInputStream( result ), input );
		}
		throw new IOException( "Could not open file or URL with " + input );
	}

	public static String cleanForFilename( final String desc ) {
		// filename = indicative name from throwable plus date/time
		String result = desc;
		result = result.replaceAll( " ", "_" );
		result = result.replaceAll( "\n", "-" );
		result = result.replaceAll( "\t", "-" );
		result = result.replaceAll( System.getProperty( "line.separator" ), "-" );
		result = result.replaceAll( ":", "-" );
		result = result.replaceAll( "/", "-" );
		result = result.replaceAll( "\\\\", "-" );
		return result;
	}

	public static File readableCanonicalFileOrNull( final File file ) {
		if ( file.exists() && file.canRead() ) {
			return canonicalFileQuietly( file );
		}
		return null;
	}

	public static File readableWriteableCanonicalDirectoryOrNull( final File directory ) {
		final File canonicalDir = canonicalFileQuietly( directory );
		if ( canonicalDir.exists() && canAll( canonicalDir ) ) {
			return canonicalDir;
		}
		if ( !canonicalDir.exists() && canAll( canonicalDir.getParentFile() ) ) {
			return canonicalDir;
		}
		return null;
	}

	private static boolean canAll( final File file ) {
		return file.canRead() && file.canWrite() && file.canExecute();
	}
}
