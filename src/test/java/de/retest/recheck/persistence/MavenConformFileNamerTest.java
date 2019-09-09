package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class MavenConformFileNamerTest {

	@Test
	void files_should_be_maven_conform() throws Exception {
		final MavenProjectLayout layout = new MavenProjectLayout();
		final Path goldenMaster = layout.getGoldenMaster( "foo", "", "bar" );
		final Path resultFile = layout.getReport( "bar" );

		assertThat( goldenMaster.toString() ).isEqualTo( "src/test/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.toString() ).isEqualTo( "target/test-classes/retest/recheck/bar.report" );
	}
}
