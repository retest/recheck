package de.retest.recheck.persistence;

import java.nio.file.Path;

import de.retest.recheck.configuration.ProjectRootFinderUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor( access = AccessLevel.PRIVATE )
public class ProjectLayouts {

	public static final String POM_XML = "pom.xml";
	public static final String BUILD_GRADLE = "build.gradle";

	public static ProjectLayout detect() {
		return ProjectRootFinderUtil.getProjectRoot() //
				.map( ProjectLayouts::findProjectBasedOnFiles ) //
				.orElseThrow( ProjectLayouts::noProjectLayoutFound );
	}

	private static ProjectLayout findProjectBasedOnFiles( final Path path ) {
		if ( exists( path, POM_XML ) ) {
			return new MavenProjectLayout();
		}
		if ( exists( path, BUILD_GRADLE ) ) {
			return new GradleProjectLayout();
		}
		return null;
	}

	private static boolean exists( final Path path, final String mavenPomFile ) {
		// https://rules.sonarsource.com/java/tag/performance/RSPEC-3725
		return path.resolve( mavenPomFile ).toFile().exists();
	}

	private static IllegalStateException noProjectLayoutFound() {
		return new IllegalStateException(
				"Could not detect project layout. Please specify one explicitly with RecheckOptionsBuilder#projectLayout." );
	}
}
