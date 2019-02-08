package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import de.retest.recheck.persistence.Persistable;
import de.retest.recheck.persistence.xml.ReTestXmlDataContainer;
import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.persistence.xml.util.StdXmlClassesProvider;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.CodeLocAttribute;
import de.retest.recheck.ui.descriptors.ContextAttribute;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.SuffixAttribute;
import de.retest.recheck.ui.descriptors.TextAttribute;
import de.retest.recheck.ui.descriptors.WeightedTextAttribute;

@RunWith( Theories.class )
public class AttributeImplPersistenceTest {

	@DataPoints
	public static final Attribute[] attributeImplementations = new Attribute[] { //
			new PathAttribute( Path.fromString( "Window/Panel_0" ) ), //
			new SuffixAttribute( 0 ), //
			new StringAttribute( "name", "my name" ), //
			new TextAttribute( "text", "some text" ), //
			new WeightedTextAttribute( "text", "some text" ), //
			new CodeLocAttribute( "[some.Method:38]" ), //
			new ContextAttribute( "some context" ), //
			OutlineAttribute.create( new Rectangle( 10, 10, 40, 20 ) ), //
			// ... and empty ones
			new PathAttribute( Path.fromString( "Window/empty" ) ), //
			new SuffixAttribute( 0 ), //
			new StringAttribute( "name", null ), //
			new TextAttribute( "text", null ), //
			new WeightedTextAttribute( "text", null ), //
			new CodeLocAttribute( null ), //
			new ContextAttribute( null ), //
			OutlineAttribute.create( null ), //
	};

	@XmlRootElement
	public static class AttributeWrapper extends Persistable {

		private static final long serialVersionUID = 1L;

		@XmlElement
		private final Attribute attribute;

		public AttributeWrapper( final Attribute attribute ) {
			super( 1 );
			this.attribute = attribute;
		}

		public AttributeWrapper() {
			super( 1 );
			attribute = null;
		}
	}

	@Theory
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public void check_persisted_and_loaded_attribtue_should_be_same( final Attribute attribute ) throws Exception {
		final XmlTransformer transformer =
				new XmlTransformer( StdXmlClassesProvider.getXmlDataClasses( AttributeWrapper.class ) );
		final String xml = transformer.toXML( new ReTestXmlDataContainer( new AttributeWrapper( attribute ) ) );
		final Attribute loaded = ((ReTestXmlDataContainer<AttributeWrapper>) transformer
				.fromXML( new ByteArrayInputStream( xml.getBytes( "utf-8" ) ) )).data().attribute;
		assertThat( attribute ).isEqualTo( loaded );
		assertThat( attribute.hashCode() ).isEqualTo( loaded.hashCode() );
		assertThat( attribute.match( loaded ) ).isEqualTo( 1.0 );
	}
}
