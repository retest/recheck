package de.retest.recheck.ignore;

import static de.retest.recheck.configuration.ProjectConfiguration.FILTER_FOLDER;
import static de.retest.recheck.configuration.ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.configuration.ProjectRootFinderUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFilterFiles {

	private static final String BASIC_FILTER_DIR = "/filter/";
	private static final String WEB_FILTER_DIR = BASIC_FILTER_DIR + "web/";
	private static final List<String> defaultWebFilter =
			Arrays.asList( WEB_FILTER_DIR + "positioning.filter", WEB_FILTER_DIR + "visibility.filter" );

	private SearchFilterFiles() {}

	private static Path resolveFilterPath() {
		final Path projectRoot = ProjectRootFinderUtil.getProjectRoot()
				.orElseThrow( () -> new RuntimeException( "Project root could not be found." ) );
		final Path projectConfigFolder = projectRoot.resolve( RETEST_PROJECT_CONFIG_FOLDER );
		final Path projectFilterFolder = projectConfigFolder.resolve( FILTER_FOLDER );
		return projectFilterFolder;
	}

	public static List<File> getDefaultFilterFiles() {
		return defaultWebFilter.stream() //
				.map( filter -> SearchFilterFiles.class.getResource( filter ) ) //
				.filter( Objects::nonNull ) //
				.map( resource -> new File( resource.getFile() ) ) //
				.collect( Collectors.toList() ); //
	}

	public static List<File> getProjectFilterFiles() {
		try ( Stream<Path> paths = Files.walk( resolveFilterPath() ) ) {
			return paths.filter( Files::isRegularFile ) //
					.filter( file -> file.toString().endsWith( ".filter" ) ) //
					.map( Path::toFile ) //
					.collect( Collectors.toList() ); //
		} catch ( final Exception e ) {
			log.info( "You can create your own filter files in the \\.retest\\filter folder" );
		}
		return new ArrayList<>();
	}
}
