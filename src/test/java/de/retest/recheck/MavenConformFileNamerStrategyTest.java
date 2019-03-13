package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.FileNamer;

class MavenConformFileNamerStrategyTest {

	@Test
	void files_should_be_maven_conform() throws Exception {
		final FileNamerStrategy cut = new MavenConformFileNamerStrategy();

		final FileNamer fileNamer = cut.createFileNamer( "foo", "bar" );
		final File recheckFile = fileNamer.getFile( Properties.RECHECK_FILE_EXTENSION );
		final File resultFile = fileNamer.getResultFile( Properties.REPORT_FILE_EXTENSION );

		assertThat( recheckFile.getPath() ).isEqualTo( "src/test/resources/retest/recheck/foo/bar.recheck" );
		assertThat( resultFile.getPath() ).isEqualTo( "target/test-classes/retest/recheck/foo/bar.report" );
	}

}
