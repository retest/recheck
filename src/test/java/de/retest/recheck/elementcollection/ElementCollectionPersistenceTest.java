package de.retest.recheck.elementcollection;

import static de.retest.recheck.util.ApprovalsUtil.verifyXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;

import org.junit.Before;
import org.junit.Test;

import de.retest.recheck.elementcollection.ElementCollection;
import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

public class ElementCollectionPersistenceTest {

	private XmlTransformer xmlTransformer;

	@Before
	public void setUp() {
		xmlTransformer = new XmlTransformer( ElementCollection.class );
	}

	@Test
	public void persisted_and_loaded_attributes_should_be_same() throws Exception {
		final ElementCollection persisted = new ElementCollection();
		final Element element = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]" ), getClass() ), new Attributes() );
		persisted.addAttribute( element, "text" );
		persisted.addAttribute( element, "color" );
		final String xml = xmlTransformer.toXML( persisted );
		final ElementCollection loaded = xmlTransformer.fromXML( new ByteArrayInputStream( xml.getBytes( "utf-8" ) ) );
		assertThat( persisted ).isEqualTo( loaded );
	}

	@Test
	public void check_persisted_xml() throws Exception {
		final ElementCollection persisted = new ElementCollection();
		final Element element = Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.fromString( "Window[1]" ), getClass() ), new Attributes() );
		persisted.addAttribute( element, "text" );
		persisted.addAttribute( element, "color" );
		final String xml = xmlTransformer.toXML( persisted );
		verifyXml( xml );
	}
}
