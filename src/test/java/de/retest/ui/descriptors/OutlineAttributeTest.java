package de.retest.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.junit.Before;
import org.junit.Test;

public class OutlineAttributeTest {

	OutlineAttribute attribute;
	Rectangle outline;

	@Before
	public void setUp() throws Exception {
		outline = new Rectangle( 1, 2, 3, 4 );
		attribute = OutlineAttribute.create( outline );
	}

	@Test
	public void getValue_should_return_equal_rectangle() throws Exception {
		assertThat( attribute.getValue() ).isEqualTo( outline );
	}

	@Test
	public void getValue_roundtrip_should_return_equal_rectangle() throws Exception {
		final StringWriter writer = new StringWriter();
		JAXB.marshal( attribute, writer );
		final StringReader xml = new StringReader( writer.getBuffer().toString() );
		final OutlineAttribute roundTrip = JAXB.unmarshal( xml, OutlineAttribute.class );

		assertThat( roundTrip.getValue() ).isEqualTo( outline );
	}

	@Test
	public void parse_roundtrip_should_return_equal_rectangle() throws Exception {
		final Rectangle normal = new Rectangle( 1, 10, 100, 0 );
		assertThat( OutlineAttribute.parse( normal.toString() ) ).isEqualTo( normal );

		final Rectangle negative = new Rectangle( -10, 10, -1, -100 );
		assertThat( OutlineAttribute.parse( negative.toString() ) ).isEqualTo( negative );
	}

	@Test
	public void parse_strange_values_should_work() throws Exception {
		// assertThat( OutlineAttribute.parse( "java.awt.Rectangle[x=-10,y=10,width=-1,height=-100]" ) )

		assertThat( OutlineAttribute.parse( null ) ).isNull();
		assertThat( OutlineAttribute.parse( "" ) ).isNull();
	}

	@Test
	public void getWeight_should_not_be_ignored() {
		assertThat( attribute.getWeight() ).isEqualTo( Attribute.NORMAL_WEIGHT );
	}

	@Test
	public void should_not_produce_greater_one_match() {
		final OutlineAttribute attributeOne = OutlineAttribute.create( new Rectangle( 27, 13, 97, 30 ) );
		final OutlineAttribute attributeTwo = OutlineAttribute.create( new Rectangle( -1000000, -999987, 1, 1 ) );
		attributeOne.match( attributeTwo );
	}

	@Test
	public void absoluteOutline_should_be_ignored() {
		final OutlineAttribute attribute = OutlineAttribute.createAbsolute( new Rectangle( 27, 13, 97, 30 ) );
		assertThat( attribute.getWeight() ).isEqualTo( Attribute.IGNORE_WEIGHT );
	}

	@Test
	public void relativeOutline_should_NOT_be_ignored() {
		final OutlineAttribute attribute = OutlineAttribute.create( new Rectangle( 27, 13, 97, 30 ) );
		assertThat( attribute.getWeight() ).isEqualTo( Attribute.NORMAL_WEIGHT );
	}
}
