package de.retest.elementcollection;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.retest.configuration.Configuration;

public class ElementCollectionFileUtilsTest {

	@Test
	public void uses_working_directory_as_folder_when_given_folder_is_null() throws IOException {
		final File folder = Configuration.getRetestWorkspace();
		final File expectedFile = new File( folder, "blacklisted.ignore" );

		final File createdFile = ElementCollectionFileUtils.getDefaultBlacklistedFile();

		assertThat( createdFile.getCanonicalFile() ).isEqualTo( expectedFile.getCanonicalFile() );
	}

}
