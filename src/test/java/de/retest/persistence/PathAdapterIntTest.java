package de.retest.persistence;

import java.io.IOException;

import org.junit.Test;

import de.retest.recheck.persistence.xml.TestPersistable;
import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.util.ApprovalsUtil;

public class PathAdapterIntTest {

	@Test
	public void xml_should_contain_path_as_String() throws IOException {
		final XmlTransformer xmlTransformer = new XmlTransformer( TestPersistable.class );
		final IdentifyingAttributes expected =
				IdentifyingAttributes.create( Path.fromString( "Window[1]/path[1]/component[1]" ), component.class );
		final String savedXML = xmlTransformer.toXML( expected );
		ApprovalsUtil.verifyXml( savedXML );
	}

	private static class component {}
}
