package de.retest.recheck.persistence.migration.transformers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.retest.recheck.util.ApprovalsUtil;

public class WindowSuffixTransformerTest {

	@Test
	public void transform_should_replace_window_with_suffixed_window() throws Exception {
		final InputStream inputStream =
				new FileInputStream( new File( "src/test/resources/migration/WindowSuffixTransformerTest.xml" ) );

		final WindowSuffixTransformer cut = new WindowSuffixTransformer();

		final InputStream transform = cut.transform( inputStream );

		ApprovalsUtil.verifyXml( IOUtils.toString( transform, "UTF-8" ) );
	}
}
