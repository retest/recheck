package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class MavenProjectLayoutTest {

	@Test
	void getTestResourceRoot_for_main_should_return_correct_path() throws Exception {
		final ProjectLayout cut = new MavenProjectLayout();

		assertThat( cut.getTestSourcesRoot() ).hasValue( Paths.get( "src/test/java" ) );
	}
}
