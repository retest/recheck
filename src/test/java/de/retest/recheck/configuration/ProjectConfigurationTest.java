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

		final Path configFolder = getProjectRoot().get().resolve( ProjectConfiguration.RETEST_PROJECT_CONFIG_FOLDER );
		final Path configFile = configFolder.resolve( ProjectConfiguration.RETEST_PROJECT_PROPERTIES );
		final Path ignpreFile = configFolder.resolve( ProjectConfiguration.RECHECK_IGNORE );

		assertThat( configFolder ).exists();
		assertThat( configFolder ).isDirectory();
		assertThat( configFile ).exists();
		assertThat( configFile ).isRegularFile();
		assertThat( ignpreFile ).exists();
		assertThat( ignpreFile ).isRegularFile();
	}

}
