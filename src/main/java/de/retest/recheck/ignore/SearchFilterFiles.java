package de.retest.recheck.ignore;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchFilterFiles {

	private static final String BASIC_FILTER_DIR = "/filter/";
	private static final String WEB_FILTER_DIR = BASIC_FILTER_DIR + "web/";
	private static final List<String> defaultWebFilter =
			Arrays.asList( WEB_FILTER_DIR + "positioning.filter", WEB_FILTER_DIR + "visibility.filter" );

	private SearchFilterFiles() {}

	public static List<File> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( filter -> SearchFilterFiles.class.getResource( filter ) ) //
				.filter( Objects::nonNull ) //
				.map( resource -> new File( resource.getFile() ) ) //
				.collect( Collectors.toList() ); //
	}

}
