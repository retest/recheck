package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import de.retest.recheck.XmlTransformerUtil;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.image.ImageUtils;
import de.retest.recheck.util.ApprovalsUtil;

class AttributesDifferenceFinderTest {

	@TempDir
	static Path tempDir;

	IdentifyingAttributes identifyingAttributes =
			IdentifyingAttributes.create( de.retest.recheck.ui.Path.fromString( "/Window[1]" ), getClass() );
	AttributesDifferenceFinder cut = new AttributesDifferenceFinder( mock( DefaultValueFinder.class ) );

	@Test
	void attributesDifference_with_identical_empty_attributes() throws Exception {
		final AttributesDifference difference =
				cut.differenceFor( element( new MutableAttributes() ), element( new MutableAttributes() ) );

		assertThat( difference ).isNull();
	}

	@Test
	void attributesDifference_with_identical_populated_attributes() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key", "value" );
		attributes1.put( "another-key", "another-value" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value" );
		attributes2.put( "another-key", "another-value" );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );

		assertThat( difference ).isNull();
	}

	@Test
	void attributesDifference_with_the_same_key_but_different_values() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key", "value1" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value2" );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );

		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo( "{key: expected=\"value1\", actual=\"value2\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void added_attribute() {
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key", "value2" );
		final AttributesDifference difference =
				cut.differenceFor( element( new MutableAttributes() ), element( attributes2 ) );

		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.toString() ).isEqualTo( "{key: expected=\"null\", actual=\"value2\"}" );
	}

	@Test
	void attributesDifference_with_different_key_and_different_values() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "value2" );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo(
				"{key1: expected=\"value1\", actual=\"null\", key2: expected=\"null\", actual=\"value2\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void attributesDifference_with_more_expected_than_actual() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		attributes1.put( "key2", "value2" );
		attributes1.put( "key3", "value3" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "different-value" );
		attributes2.put( "key1", "value1" );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.toString() ).contains( "key2: expected=\"value2\", actual=\"different-value\"" );
		assertThat( difference.toString() ).contains( "key3: expected=\"value3\", actual=\"null\"" );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void attributesDifference_with_less_expected_than_actual() {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( "key1", "value1" );
		attributes1.put( "key2", "value2" );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( "key2", "different-value" );
		attributes2.put( "key1", "value1" );
		attributes2.put( "key3", "value3" );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );

		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );
		assertThat( difference.getNonEmptyDifferences() ).isEmpty();
		assertThat( difference.toString() ).isEqualTo(
				"{key2: expected=\"value2\", actual=\"different-value\", key3: expected=\"null\", actual=\"value3\"}" );

		ApprovalsUtil.verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void for_nondefault_values_expected_should_be_default() throws Exception {
		final MutableAttributes nondefault = new MutableAttributes();
		nondefault.put( "font", "Lucida Grance-italic-123" );
		nondefault.put( "background", "#FF0000" );
		nondefault.put( "foreground", "#00FF00" );

		final MutableAttributes defaults = new MutableAttributes();
		defaults.put( "font", "Arial" );
		defaults.put( "background", "#FFFFFF" );
		defaults.put( "foreground", "#000000" );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );
		final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( dvf );

		final AttributesDifference difference = cut.differenceFor( element( defaults ), element( nondefault ) );

		assertThat( difference.expected() ).hasToString( "{background=#FFFFFF, font=Arial, foreground=#000000}" );
	}

	@Test
	void default_values_should_not_cause_difference() throws Exception {
		final String attributesKey = "foo";
		final String attributesValue = "bar";
		final MutableAttributes mutable = new MutableAttributes();
		mutable.put( attributesKey, attributesValue );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );
		when( dvf.isDefaultValue( identifyingAttributes, attributesKey, attributesValue ) ).thenReturn( true );

		final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( dvf );

		final Element expected = element( new MutableAttributes() );
		final Element actual = element( mutable );
		assertThat( cut.differenceFor( expected, actual ) ).isNull();
	}

	@Test
	void same_screenshot_should_not_cause_difference() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		final BufferedImage img = createImg( Color.BLACK );
		attributes1.put( ImageUtils.image2Screenshot( "", img ) );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( ImageUtils.image2Screenshot( "", img ) );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );
		assertThat( difference ).isNull();
	}

	@Test
	void different_screenshots_should_cause_difference() throws Exception {
		final MutableAttributes attributes1 = new MutableAttributes();
		attributes1.put( ImageUtils.image2Screenshot( "", createImg( Color.BLACK ) ) );
		final MutableAttributes attributes2 = new MutableAttributes();
		attributes2.put( ImageUtils.image2Screenshot( "", createImg( Color.WHITE ) ) );
		final AttributesDifference difference = cut.differenceFor( element( attributes1 ), element( attributes2 ) );
		assertThat( difference.size() ).isEqualTo( 1 );
	}

	@Test
	void deleted_attributes_should_cause_difference_even_if_default() throws Exception {
		final String key = "key";
		final String expected = "value";
		final String actual = null;

		final MutableAttributes as0 = new MutableAttributes();
		as0.put( key, expected );
		final Element e0 = element( as0 );

		final MutableAttributes as1 = new MutableAttributes();
		// "key" deleted.
		final Element e1 = element( as1 );

		final DefaultValueFinder dvf = mock( DefaultValueFinder.class );
		when( dvf.isDefaultValue( any(), eq( key ), isNull() ) ).thenReturn( true );

		final AttributesDifferenceFinder cut = new AttributesDifferenceFinder( dvf );

		final AttributesDifference diff = cut.differenceFor( e0, e1 );
		assertThat( diff.getDifferences() ).containsExactly( new AttributeDifference( key, expected, actual ) );
	}

	private Element element( final MutableAttributes ma ) {
		return Element.create( "id", mock( Element.class ), identifyingAttributes, ma.immutable() );
	}

	private BufferedImage createImg( final Color color ) throws IOException {
		final BufferedImage result = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_RGB );
		final Graphics2D g2 = result.createGraphics();
		g2.setColor( color );
		g2.fillRect( 0, 0, 20, 20 );

		final Path img = tempDir.resolve( System.currentTimeMillis() + ".png" );
		Files.createFile( img );
		ImageIO.write( result, "PNG", img.toFile() );

		return result;
	}
}
