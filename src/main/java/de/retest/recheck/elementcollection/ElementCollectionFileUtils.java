package de.retest.recheck.elementcollection;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

import de.retest.recheck.configuration.Configuration;

public class ElementCollectionFileUtils {

	public static final String BLACKLISTED_FILENAME = "blacklisted";
	public static final String WHITELISTED_FILENAME = "whitelisted";
	public static final String IGNORED_FILENAME = "recheck";
	public static final String IGNORED_DIFFS_FILE = "de.retest.ignored.File";

	public static final String FILE_EXTENSION = "ignore";
	public static final FileNameExtensionFilter FILE_EXTENSION_FILTER =
			new FileNameExtensionFilter( "Ignored Differences File Extension", FILE_EXTENSION );

	public static final FilenameFilter FILE_FILTER = new FilenameFilter() {
		@Override
		public boolean accept( final File dir, final String name ) {
			return name.endsWith( FILE_EXTENSION );
		}
	};

	public static File getDefaultBlacklistedFile() {
		return new File( Configuration.getRetestWorkspace(), BLACKLISTED_FILENAME + "." + FILE_EXTENSION );
	}

	public static File getDefaultWhitelistedFile() {
		return new File( Configuration.getRetestWorkspace(), WHITELISTED_FILENAME + "." + FILE_EXTENSION );
	}

	public static String getNameWithoutExtension( final File excludedCompsFile ) {
		return excludedCompsFile.getName().replace( "." + FILE_EXTENSION, "" );
	}

	public static File getIgnoredDiffsFile() {
		final String ignoredFileProp =
				System.getProperty( IGNORED_DIFFS_FILE, IGNORED_FILENAME + "." + FILE_EXTENSION );
		return new File( ignoredFileProp );
	}
}
