package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.configuration.ProjectConfiguration;

public class SearchFilterFiles {

	public static final String FILES_ENDING = ".filter";
	private static final String BASIC_FILTER_DIR = "/filter/";
	private static final String WEB_FILTER_DIR = BASIC_FILTER_DIR + "web/";
	private static final List<String> defaultWebFilter =
			Arrays.asList( WEB_FILTER_DIR + "positioning.filter", WEB_FILTER_DIR + "visibility.filter" );

	private SearchFilterFiles() {}

	private static Path resolveFilterPath() {
		return ProjectConfiguration.getInstance().findProjectConfigFolder().resolve( FILTER_FOLDER );
	}

	public static List<File> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( filter -> SearchFilterFiles.class.getResource( filter ) ) //
				.filter( Objects::nonNull ) //
				.map( resource -> new File( resource.getFile() ) ) //
				.collect( Collectors.toList() ); //
	}

	public static List<File> getProjectFilterFiles() throws IOException {
		if ( !resolveFilterPath().toFile().exists() ) {
			return Collections.emptyList();
		}
		try ( Stream<Path> paths = Files.walk( resolveFilterPath() ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( file -> file.toString().endsWith( FILES_ENDING ) ) //
					.map( Path::toFile ) //
					.collect( Collectors.toList() ); //
		}
	}
}
