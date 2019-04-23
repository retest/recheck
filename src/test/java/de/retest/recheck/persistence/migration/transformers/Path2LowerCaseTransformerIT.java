package de.retest.recheck.persistence.migration.transformers;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.migration.XmlTransformer;
import de.retest.recheck.util.ApprovalsUtil;

class Path2LowerCaseTransformerIT {

	@Test
	void path_should_be_transformed() throws Exception {
		final InputStream inputStream =
				new FileInputStream( new File( "src/test/resources/migration/Path2LowerCaseTransformer.xml" ) );

		final XmlTransformer transformer = new Path2LowerCaseTransformer();
		final InputStream transform = transformer.transform( inputStream );
		ApprovalsUtil.verifyXml( IOUtils.toString( transform, UTF_8.name() ) );
	}
}
