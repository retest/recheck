package de.retest.persistence.xml;

import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;

import javax.swing.JButton;

import org.junit.Test;

import de.retest.elementcollection.ElementCollection;
import de.retest.persistence.xml.util.StdXmlClassesProvider;
import de.retest.ui.Path;
import de.retest.ui.descriptors.Attributes;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.util.ApprovalsUtil;

public class AttributesAdapterTest {

	@Test
	public void inheriting_attributes_should_be_persistable() throws FileNotFoundException {
		// load xml
		final XmlTransformer xmlTransformer = new XmlTransformer( StdXmlClassesProvider.getXmlDataClasses() );

		final ElementCollection data = new ElementCollection();
		data.add( Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window/path/component[1]" ), JButton.class ),
				new Attributes() ) );
		final ReTestXmlDataContainer<ElementCollection> dataContainer = new ReTestXmlDataContainer<>( data );

		// persist xml
		final String xml = xmlTransformer.toXML( dataContainer );

		// approve xml
		ApprovalsUtil.verifyXml( xml );
	}

}
