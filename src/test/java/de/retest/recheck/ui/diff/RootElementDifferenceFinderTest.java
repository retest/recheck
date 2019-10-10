package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.diff.ElementBuilder.buildElement;
import static de.retest.recheck.util.ApprovalsUtil.verify;
import static de.retest.recheck.util.ApprovalsUtil.verifyXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.XmlTransformerUtil;
import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;

class RootElementDifferenceFinderTest {

	private final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );

	private final IdentifyingAttributes identifyingAttributesA =
			IdentifyingAttributes.create( Path.path( new PathElement( "Window", 1 ) ), Window.class );
	private final IdentifyingAttributes identifyingAttributesB =
			IdentifyingAttributes.create( Path.path( new PathElement( "Window", 1 ) ), OtherWindow.class );

	private final Attributes attributes = new Attributes();
	private final MutableAttributes otherAttributes = new MutableAttributes();
	private final RootElementDifferenceFinder rootElementDifferenceFinder =
			new RootElementDifferenceFinder( mock( Environment.class ) );

	@BeforeEach
	public void setup() {
		otherAttributes.put( "criterion", Boolean.TRUE );
	}

	@Test
	void expected_root_element_is_null() {
		final RootElement actualDescriptor = descriptorFor( identifyingAttributesA, new Attributes(), screenshot );

		final RootElementDifference difference = rootElementDifferenceFinder.findDifference( null, actualDescriptor );

		assertThat( difference.elementDifference.actualScreenshot ).isEqualTo( screenshot );
		assertThat( difference.elementDifference.expectedScreenshot ).isNull();
		assertThat( difference.elementDifference.getIdentifyingAttributes() ).isEqualTo( identifyingAttributesA );
		assertThat( difference.getNonEmptyDifferences() ).containsExactly( difference.elementDifference );
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 1 );
		assertThat( difference.elementDifference.childDifferences ).containsExactly();

		verify( difference );
		verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void actual_root_element_is_null() {
		final RootElement expectedDescriptor = descriptorFor( identifyingAttributesA, new Attributes(), screenshot );

		final RootElementDifference difference = rootElementDifferenceFinder.findDifference( expectedDescriptor, null );

		assertThat( difference.elementDifference.actualScreenshot ).isNull();
		assertThat( difference.elementDifference.expectedScreenshot ).isEqualTo( screenshot );
		assertThat( difference.elementDifference.getIdentifyingAttributes() ).isEqualTo( identifyingAttributesA );
		assertThat( difference.getNonEmptyDifferences() ).containsExactly( difference.elementDifference );
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 1 );
		assertThat( difference.elementDifference.childDifferences ).containsExactly();

		verify( difference );
		verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void expected_and_actual_root_element_are_identical() {
		final RootElement descriptor = descriptorFor( identifyingAttributesA, attributes, screenshot );

		final RootElementDifference differences = rootElementDifferenceFinder.findDifference( descriptor, descriptor );

		assertThat( differences ).isNull();
	}

	@Test
	void expected_and_actual_root_element_are_different() {

		final RootElement expectedDescriptor = descriptorFor( identifyingAttributesA, attributes, screenshot );

		final RootElement actualDescriptor = descriptorFor( identifyingAttributesB, attributes, screenshot );

		final RootElementDifference difference =
				rootElementDifferenceFinder.findDifference( expectedDescriptor, actualDescriptor );

		assertThat( difference.elementDifference.actualScreenshot ).isEqualTo( screenshot );
		assertThat( difference.elementDifference.expectedScreenshot ).isEqualTo( screenshot );
		assertThat( difference.elementDifference.getIdentifyingAttributes() ).isEqualTo( identifyingAttributesA );
		assertThat( difference.getNonEmptyDifferences() ).containsExactly( difference.elementDifference );
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 1 );
		assertThat( difference.elementDifference.childDifferences ).containsExactly();

		verify( difference );
		verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( difference ) );
	}

	@Test
	void identical_root_element_are_matched() {
		final RootElement descriptor = descriptorFor( identifyingAttributesA, attributes, screenshot );

		final List<RootElementDifference> differences =
				rootElementDifferenceFinder.findDifferences( Arrays.asList( descriptor ), Arrays.asList( descriptor ) );

		assertThat( differences.isEmpty() ).isTrue();
	}

	@Test
	void lists_of_identical_root_element_are_matched_even_if_the_order_is_different() {
		final RootElement descriptor1 = descriptorFor( identifyingAttributesA, attributes, screenshot );
		final RootElement descriptor2 = descriptorFor( identifyingAttributesB, attributes, screenshot );

		final List<RootElementDifference> differences = rootElementDifferenceFinder.findDifferences(
				Arrays.asList( descriptor1, descriptor2 ), Arrays.asList( descriptor2, descriptor1 ) );

		assertThat( differences.isEmpty() ).isTrue();
	}

	@Test
	void lists_of_different_root_element_are_matched_even_if_the_order_is_different() {
		final RootElement descriptor_AS = descriptorFor( identifyingAttributesA, attributes, screenshot );
		final RootElement descriptor_AO =
				descriptorFor( identifyingAttributesA, otherAttributes.immutable(), screenshot );
		final RootElement descriptor_BS = descriptorFor( identifyingAttributesB, attributes, screenshot );
		final RootElement descriptor_BO =
				descriptorFor( identifyingAttributesB, otherAttributes.immutable(), screenshot );

		final List<RootElementDifference> differences = rootElementDifferenceFinder.findDifferences(
				Arrays.asList( descriptor_AS, descriptor_BS ), Arrays.asList( descriptor_BO, descriptor_AO ) );

		assertThat( differences.isEmpty() ).isFalse();
		assertThat( ElementDifferenceFinder.getElementDifferences( differences ).size() ).isEqualTo( 2 );
		verify( ElementDifferenceFinder.getNonEmptyDifferences( differences ) );
	}

	@Test
	void lists_of_root_element_with_different_window_titles_are_not_matched() {
		final RootElement descriptor_1 = descriptorFor( identifyingAttributesA, attributes, screenshot );
		final RootElement descriptor_2 = descriptorFor( identifyingAttributesB, attributes, screenshot );

		final List<RootElementDifference> differences = rootElementDifferenceFinder.findDifferences(
				Collections.singletonList( descriptor_1 ), Collections.singletonList( descriptor_2 ) );

		assertThat( differences.isEmpty() ).isFalse();
		assertThat( ElementDifferenceFinder.getElementDifferences( differences ).size() ).isEqualTo( 2 );
		verify( ElementDifferenceFinder.getNonEmptyDifferences( differences ) );
	}

	@Test
	void different_root_element_with_same_components_should_match() {
		final IdentifyingAttributes identifyingAttributes1 =
				IdentifyingAttributes.create( Path.path( new PathElement( "Window", 1 ) ), Window.class );
		final IdentifyingAttributes identifyingAttributes2 =
				IdentifyingAttributes.create( Path.path( new PathElement( "Other", 1 ) ), JDialog.class );
		final RootElement descriptor1 = descriptorFor( identifyingAttributes1, attributes, screenshot, buildElement() );
		final RootElement descriptor2 = descriptorFor( identifyingAttributes2, attributes, screenshot, buildElement() );

		final List<RootElementDifference> differences = rootElementDifferenceFinder
				.findDifferences( Collections.singletonList( descriptor1 ), Collections.singletonList( descriptor2 ) );

		assertThat( differences.isEmpty() ).isFalse();
		verifyXml( XmlTransformerUtil.toXmlFragmentViaJAXB( differences ) );
	}

	@Test
	void two_null_arguments() {
		assertThat( cut.findDifference( (RootElement) null, null ) ).isNull();
	}

	private RootElement descriptorFor( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot, final Element... childrenArray ) {
		List<Element> children = new ArrayList<>();
		if ( childrenArray != null ) {
			children = Arrays.asList( childrenArray );
		}
		final RootElement rootElement = new RootElement( "id", identifyingAttributes, attributes, screenshot, null,
				identifyingAttributes.getType().hashCode(), "Window" );
		rootElement.addChildren( children );
		return rootElement;
	}

	private static class Window {}

	private class OtherWindow {}
}
