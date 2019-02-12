package de.retest.recheck.persistence.migration.transformers;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.migration.XmlTransformer;
import de.retest.recheck.persistence.migration.transformers.AddRetestIdTestTransformer.RetestIdCreator;
import de.retest.recheck.util.ApprovalsUtil;

class AddRetestIdTestTransformerTest {

	@Test
	void retestId_should_be_added() throws Exception {
		final InputStream inputStream =
				new FileInputStream( new File( "src/test/resources/migration/AddRetestIdTestTransformer.xml" ) );

		final XmlTransformer transformer = new AddRetestIdTestTransformer( new RetestIdCreator() {
			private int counter = 0;

			@Override
			public String retestId() {
				return Integer.toString( counter++ );
			}
		} );
		final InputStream transform = transformer.transform( inputStream );
		ApprovalsUtil.verifyXml( IOUtils.toString( transform, UTF_8.name() ) );
	}
}
