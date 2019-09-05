package de.retest.recheck.persistence.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.SutState;

class XmlTransformerTest {

	@Test
	void test() throws Exception {
		final String base = "src/test/resources/de/retest/recheck/persistence/xml/";

		final XmlTransformer cut = new XmlTransformer( StdXmlClassesProvider.getXmlDataClasses() );

		// Load first XML (we don't care about the result).
		final Path state0 = Paths.get( base, "XmlTransformerTest.test.state0.xml" );
		cut.fromXML( Files.newInputStream( state0 ), null );

		// Load second XML.
		final Path state1 = Paths.get( base, "XmlTransformerTest.test.state1.xml" );
		@SuppressWarnings( "unchecked" )
		final ReTestXmlDataContainer<SutState> result =
				(ReTestXmlDataContainer<SutState>) cut.fromXML( Files.newInputStream( state1 ), null );

		final Element meta3 = result.data() // SutState
				.getRootElements().get( 0 ) // html[1]
				.getContainedElements().get( 0 ) // head[1]
				.getContainedElements().get( 1 ); // meta[3]

		/*
		 * XXX See path in line 130, retest ID in line 114, both in state1.xml. If we reuse the JAXBContext in
		 * XmlTransformer, then the retest ID becomes the one from html[1]/head[1]/meta[3] in state0.xml.
		 */
		assertThat( meta3.getIdentifyingAttributes().getPath() ).isEqualTo( "html[1]/head[1]/meta[3]" );
		assertThat( meta3.getRetestId() ).isEqualTo( "meta-afc35" );
	}

}
