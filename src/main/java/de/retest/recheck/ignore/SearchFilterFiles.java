package de.retest.recheck.ignore;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFilterFiles {

	private static final String PATH_FILTER_FILES = "filter/web/";
	private static final List<String> DEFAULT_FILTER_FILES = Arrays.asList( "positioning.filter", "visibility.filter" );

	private SearchFilterFiles() {}

	public static List<File> getDefaultFilterFiles() {
		final List<File> solution = new ArrayList<>();
		for ( final String filterName : DEFAULT_FILTER_FILES ) {
			final URL resource = SearchFilterFiles.class.getClassLoader().getResource( PATH_FILTER_FILES + filterName );
			if ( resource == null ) {
				log.debug( "The search for {} was unsuccessful, make sure that the file was not deleted.", filterName );
			} else {
				final File filterFile = new File( resource.getFile() );
				solution.add( filterFile );
			}
		}
		return solution;
	}

}
