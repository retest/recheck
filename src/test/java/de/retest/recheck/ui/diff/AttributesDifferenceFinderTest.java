package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import de.retest.recheck.XmlTransformerUtil;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.image.ImageUtils;
import de.retest.recheck.util.ApprovalsUtil;

public class AttributesDifferenceFinderTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	private final IdentifyingAttributes identifyingAttributes =
			IdentifyingAttributes.create( Path.fromString( "/Window[1]" ), getClass() );
	private final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( mock( DefaultValueFinder.class ) );

	@Test
	public void attributesDifference_with_identical_empty_attributes() throws Exception {
		final AttributesDifference difference =
				cut.differenceFor( element( new Attributes() ), element( new Attributes() ) );

		assertThat( difference ).isNull();
	}

	@Test
	public void attributesDifference_with_identical_populated_attributes() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key", "value" );
		attributes1.put( "another-key", "another-value" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value" );
		attributes2.put( "another-key", "another-value" );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );

		assertThat( difference ).isNull();
	}

	@Test
	public void attributesDifference_with_the_same_key_but_different_values() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key", "value1" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value2" );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );

		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo( "{key: expected=\"value1\", actual=\"value2\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	public void added_attribute() {
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value2" );
		final AttributesDifference difference =
				cut.differenceFor( element( new MutableAttributes().immutable() ), element( attributes2.immutable() ) );

		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.toString() ).isEqualTo( "{key: expected=\"null\", actual=\"value2\"}" );
	}

	@Test
	public void attributesDifference_with_different_key_and_different_values() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "value2" );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo(
				"{key1: expected=\"value1\", actual=\"null\", key2: expected=\"null\", actual=\"value2\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	public void attributesDifference_with_more_expected_than_actual() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		attributes1.put( "key2", "value2" );
		attributes1.put( "key3", "value3" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "different-value" );
		attributes2.put( "key1", "value1" );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.toString() ).contains( "key2: expected=\"value2\", actual=\"different-value\"" );
		assertThat( difference.toString() ).contains( "key3: expected=\"value3\", actual=\"null\"" );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	public void attributesDifference_with_less_expected_than_actual() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		attributes1.put( "key2", "value2" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "different-value" );
		attributes2.put( "key1", "value1" );
		attributes2.put( "key3", "value3" );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo(
				"{key2: expected=\"value2\", actual=\"different-value\", key3: expected=\"null\", actual=\"value3\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	public void for_nondefault_values_expected_should_be_default() throws Exception {
		final MutableAttributes nondefault = new MutableAttributes();
		nondefault.put( "font", "Lucida Grance-italic-123" );
		nondefault.put( "background", "#FF0000" );
		nondefault.put( "foreground", "#00FF00" );

		final MutableAttributes defaults = new MutableAttributes();
		defaults.put( "font", "Arial" );
		defaults.put( "background", "#FFFFFF" );
		defaults.put( "foreground", "#000000" );

		final IdentifyingAttributes identifyingAttributes =
				IdentifyingAttributes.create( Path.fromString( "/Window[1]" ), getClass() );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );

		final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( dvf );
		final Attributes attributes = new Attributes();

		final AttributesDifference difference =
				cut.differenceFor( element( defaults.immutable() ), element( nondefault.immutable() ) );
		assertThat( difference.expected().toString() )
				.isEqualTo( "{background=#FFFFFF, font=Arial, foreground=#000000}" );
	}

	@Test
	public void default_values_should_not_cause_difference() throws Exception {
		final String attributesKey = "foo";
		final String attributesValue = "bar";
		final MutableAttributes mutable = new MutableAttributes();
		mutable.put( attributesKey, attributesValue );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );
		when( dvf.isDefaultValue( identifyingAttributes, attributesKey, attributesValue ) ).thenReturn( true );

		final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( dvf );

		final Element expected = element( new Attributes() );
		final Element actual = element( mutable.immutable() );
		assertThat( cut.differenceFor( expected, actual ) ).isNull();
	}

	public void same_screenshot_should_not_cause_difference() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		final BufferedImage img = createImg( Color.BLACK );
		attributes1.put( ImageUtils.image2Screenshot( "", img ) );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( ImageUtils.image2Screenshot( "", img ) );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );
		assertThat( difference ).isNull();
	}

	@Test
	public void different_screenshots_should_cause_difference() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( ImageUtils.image2Screenshot( "", createImg( Color.BLACK ) ) );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( ImageUtils.image2Screenshot( "", createImg( Color.WHITE ) ) );
		final AttributesDifference difference =
				cut.differenceFor( element( attributes1.immutable() ), element( attributes2.immutable() ) );
		assertThat( difference.size() ).isEqualTo( 1 );
	}

	private Element element( final Attributes attributes ) {
		return Element.create( "id", mock( Element.class ), identifyingAttributes, attributes );
	}

	private BufferedImage createImg( final Color color ) {
		final BufferedImage result = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_RGB );
		final Graphics2D g2 = result.createGraphics();
		g2.setColor( color );
		g2.fillRect( 0, 0, 20, 20 );
		try {
			ImageIO.write( result, "PNG", temp.newFile( "target/" + color + ".png" ) );
		} catch ( final IOException e ) {}
		return result;
	}
}
