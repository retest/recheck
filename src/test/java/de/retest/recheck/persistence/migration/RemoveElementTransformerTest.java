package de.retest.recheck.persistence.migration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.retest.recheck.util.ApprovalsUtil;

public class RemoveElementTransformerTest {

	@Test
	public void simple_xml_element_should_be_removed() throws Exception {
		final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" //
				+ "<root>\n" //
				+ "\t<parent>\n" //
				+ "\t\t<remove>Test</remove>\n" //
				+ "\t</parent>\n" //
				+ "\t<remove>Test</remove>\n" //
				+ "</root>";
		final RemoveElementTransformer transformer = new RemoveElementTransformer( "remove" );

		final InputStream transform = transformer.transform( new ByteArrayInputStream( xml.getBytes() ) );

		ApprovalsUtil.verifyXml( IOUtils.toString( transform, "UTF-8" ) );
	}

	@Test
	public void extended_xml_should_be_removed_correctly() throws Exception {
		final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" //
				+ "<root>\n" //
				+ "\t<parent>\n" //
				+ "\t\t<remove>Test1</remove>\n" //
				+ "\t\t<remove>Test2</remove>\n" //
				+ "\t\t<keep>Test</keep>\n" //
				+ "\t\t<remove>Test3</remove>\n" //
				+ "\t</parent>\n" //
				+ "\t<remove>Test3</remove>\n" //
				+ "</root>";
		final RemoveElementTransformer transformer = new RemoveElementTransformer( "parent", "remove" );

		final InputStream transform = transformer.transform( new ByteArrayInputStream( xml.getBytes() ) );

		ApprovalsUtil.verifyXml( IOUtils.toString( transform, "UTF-8" ) );
	}
}
