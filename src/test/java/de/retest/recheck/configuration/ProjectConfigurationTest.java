package de.retest.recheck.configuration;

import static de.retest.recheck.configuration.ProjectRootFinderUtil.getProjectRoot;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ProjectConfigurationTest {

	ProjectConfiguration cut = ProjectConfiguration.getInstance();

	@Test
	void local_config_file_should_be_created() throws Exception {
		cut.ensureProjectConfigurationInitialized();

		final Path projectConfigFolder = getProjectRoot().resolve( ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER );
		final Path projectConfigProperties =
				projectConfigFolder.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES );

		assertThat( projectConfigFolder ).exists();
		assertThat( projectConfigFolder ).isDirectory();

		assertThat( projectConfigProperties ).exists();
		assertThat( projectConfigProperties ).isRegularFile();
	}

}
