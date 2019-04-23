package de.retest.recheck.ignore;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.PathBasedProjectRootFinder;

public class SearchFilterFiles {
	private static final Logger logger = LoggerFactory.getLogger( PathBasedProjectRootFinder.class );

	public List<File> getDefaultFilterFiles() {
		final List<File> solution = new ArrayList<>();
		final String[] a = { "positioning.filter", "visibility.filter" };
		for ( int i = 0; i <= a.length - 1; i++ ) {
			final URL resource = getClass().getClassLoader().getResource( "filter/web/" + a[i] );
			if ( resource == null ) {
				logger.debug( "The search for {} was unsuccessful, make sure that the file was not deleted.", a[i] );

			} else {
				final File filterFile = new File( resource.getFile() );
				solution.add( filterFile );
			}
		}
		return solution;
	}
}
