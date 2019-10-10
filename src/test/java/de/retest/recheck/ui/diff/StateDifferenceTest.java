package de.retest.recheck.ui.diff;

import static de.retest.recheck.XmlTransformerUtil.toXmlFragmentViaJAXB;
import static de.retest.recheck.util.ApprovalsUtil.verifyXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.image.Screenshot.ImageType;

@Disabled( "see RET-1549" )
class StateDifferenceTest {

	private final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );
	private final Attributes attributes = new Attributes();

	private final IdentifyingAttributes identifyingAttributesA =
			IdentifyingAttributes.create( Path.path( new PathElement( "Window", 1 ) ), Window.class );
	private final IdentifyingAttributes identifyingAttributesB =
			IdentifyingAttributes.create( Path.path( new PathElement( "Window", 1 ) ), OtherWindow.class );

	private final RootElement descriptorA = descriptorFor( identifyingAttributesA, attributes, screenshot );
	private final RootElement descriptorB = descriptorFor( identifyingAttributesB, attributes, screenshot );

	private final RootElementDifferenceFinder rootElementDifferenceFinder =
			new RootElementDifferenceFinder( mock( Environment.class ) );

	@Test
	void listDifference_with_one_entry_each() {
		final StateDifference cut = new StateDifference(
				Collections.singletonList( rootElementDifferenceFinder.findDifference( descriptorA, descriptorB ) ) );

		assertThat( cut.size() ).isEqualTo( 1 );
		assertThat( cut.getNonEmptyDifferences().size() ).isEqualTo( 1 );
		assertThat( cut.toString() ).isEqualTo(
				"[nullIdentifyingAttributesDifference expected type: de.retest.recheck.ui.diff.StateDifferenceTest$Window - actual type: de.retest.recheck.ui.diff.StateDifferenceTest$OtherWindow]" );
		assertThat( cut.getElementDifferences().size() ).isEqualTo( 1 );
		verifyXml( toXmlFragmentViaJAXB( cut, StateDifference.class ) );
	}

	@Test
	void listDifference_with_many_entries_some_of_which_differ() {
		final StateDifference cut = new StateDifference(
				Collections.singletonList( rootElementDifferenceFinder.findDifference( descriptorA, descriptorB ) ) );

		assertThat( cut.size() ).isEqualTo( 1 );
		assertThat( cut.getNonEmptyDifferences().size() ).isEqualTo( 1 );
		assertThat( cut.toString() ).isEqualTo(
				"[nullIdentifyingAttributesDifference expected type: de.retest.recheck.ui.diff.StateDifferenceTest$Window - actual type: de.retest.recheck.ui.diff.StateDifferenceTest$OtherWindow]" );
		assertThat( cut.getElementDifferences().size() ).isEqualTo( 1 );

		verifyXml( toXmlFragmentViaJAXB( cut, StateDifference.class ) );
	}

	private RootElement descriptorFor( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot ) {
		final RootElement descriptor = mock( RootElement.class );
		when( descriptor.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( descriptor.getAttributes() ).thenReturn( attributes );
		when( descriptor.getScreenshot() ).thenReturn( screenshot );
		return descriptor;
	}

	private static class Window {}

	private class OtherWindow {}
}
