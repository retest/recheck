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
	private static final String[] DEFAULT_FILTER_FILES = { "positioning.filter", "visibility.filter" };

	public List<File> getDefaultFilterFiles() {
		final List<File> solution = new ArrayList<>();
		for ( final String filterName : DEFAULT_FILTER_FILES ) {
			final URL resource = getClass().getClassLoader().getResource( "filter/web/" + filterName );
			if ( resource == null ) {
				logger.debug( "The search for {} was unsuccessful, make sure that the file was not deleted.",
						filterName );
			} else {
				final File filterFile = new File( resource.getFile() );
				solution.add( filterFile );
			}
		}
		return solution;
	}

}
