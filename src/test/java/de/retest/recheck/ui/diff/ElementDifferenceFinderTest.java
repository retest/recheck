package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.Path.fromString;
import static de.retest.recheck.ui.diff.ElementBuilder.buildElement;
import static de.retest.recheck.ui.diff.ElementBuilder.createIdentifyingAttribute;
import static de.retest.recheck.ui.diff.ElementBuilder.toAttributes;
import static de.retest.recheck.ui.diff.ElementDifferenceFinder.getElementDifferences;
import static de.retest.recheck.ui.diff.ElementDifferenceFinder.getNonEmptyDifferences;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.ElementBuilder.child1;
import de.retest.recheck.ui.diff.ElementBuilder.child2;
import de.retest.recheck.ui.diff.ElementBuilder.child3;
import de.retest.recheck.ui.diff.ElementBuilder.comp1;

class ElementDifferenceFinderTest {

	@Test
	void path_differences_in_children_should_result_in_difference() throws Exception {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element parent = mock( Element.class );
		final Attributes attributes = new Attributes();

		// window/path[1]/comp[1]
		final Element element = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window/path[1]/comp[1]" ), Type.class ), attributes );

		final Element path = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window/path[1]" ), Comp.class ), attributes );
		path.addChildren( element );

		final Element window = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window[1]" ), Type.class ), attributes );
		window.addChildren( path );

		// window/path[2]/comp[1]
		final Element otherElement = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window/path[2]/comp[1]" ), Type.class ), attributes );

		final Element otherPath = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window/path[2]" ), Comp.class ), attributes );
		otherPath.addChildren( otherElement );

		final Element otherWindow = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "window[1]" ), Type.class ), attributes );
		otherWindow.addChildren( otherPath );

		// should result in one difference: path[1] and path[2]
		final Collection<ElementDifference> differences = cut.findChildDifferences( window, otherWindow );
		final ElementDifference diff = differences.iterator().next();

		assertThat( diff.toString() ).isEqualTo( //
				"ElementDifferenceFinderTest$Comp:" //
						+ "\n at: window[1]/path[1]:" //
						+ "\n\texpected path: window[1]/path[1] - actual path: window[1]/path[2]" );
	}

	@Test
	void same_component_should_produce_no_difference() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();
		final Element actual = buildElement();

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNull();
	}

	@Test
	void different_state_in_first_child() throws Exception {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = buildElement();
		actual.getContainedElements().remove( 0 );

		final Element child1 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child1.class ) ),
				toAttributes( "{color=yellow}" ) );
		actual.addChildren( child1 );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 2 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.getContainedElements().get( 0 ) ).isNotEqualTo( expected.getContainedElements().get( 0 ) );
		assertThat( difference.toString() ).contains( "color: expected=\"red\", actual=\"yellow\"" );
	}

	@Test
	void different_state_in_child_attribute() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = buildElement();
		actual.getContainedElements().remove( 1 );

		final Element child2 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child2.class ) ),
				toAttributes( "{color=yellow}" ) );
		actual.addChildren( child2 );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 2 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.getContainedElements().get( 1 ) ).isNotEqualTo( expected.getContainedElements().get( 1 ) );
		assertThat( difference.toString() ).contains( "color: expected=\"green\", actual=\"yellow\"" );
	}

	@Test
	void different_state_in_last_child() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = buildElement();
		actual.getContainedElements().remove( 2 );

		final Element child3 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child3.class ) ),
				toAttributes( "{color=yellow}" ) );
		actual.addChildren( child3 );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 2 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.getContainedElements().get( 2 ) ).isNotEqualTo( expected.getContainedElements().get( 2 ) );
		assertThat( difference.toString() ).contains( "color: expected=\"violett\", actual=\"yellow\"" );
	}

	@Test
	void different_children_abc_dbe() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( null, comp1.class ) ),
				toAttributes( "{color=blue}" ) );

		final Element actualChild1 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), otherChild1.class ) ),
				toAttributes( "{color=red}" ) );
		final Element actualChild2 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child2.class ) ),
				toAttributes( "{color=green}" ) );
		final Element actualChild3 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), otherChild3.class ) ),
				toAttributes( "{color=violett}" ) );

		final List<Element> containedComponents = Arrays.asList( actualChild1, actualChild2, actualChild3 );
		actual.addChildren( containedComponents );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 2 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 3 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.getContainedElements().get( 0 ) ).isNotEqualTo( expected.getContainedElements().get( 0 ) );
		assertThat( actual.getContainedElements().get( 1 ) ).isEqualTo( expected.getContainedElements().get( 1 ) );
		assertThat( actual.getContainedElements().get( 2 ) ).isNotEqualTo( expected.getContainedElements().get( 2 ) );
	}

	@Test
	void children_of_different_components() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1a" ), comp1a.class ) ),
				toAttributes( "{color=blue}" ) );

		final Element actualChild1 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1a/child1" ), child1.class ) ),
				toAttributes( "{color=red}" ) );
		final Element actualChild2 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1a/child2" ), child2.class ) ),
				toAttributes( "{color=yellow}" ) );
		final Element actualChild3 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1a/child3" ), child3.class ) ),
				toAttributes( "{color=black}" ) );

		final List<Element> containedComponents = Arrays.asList( actualChild1, actualChild2, actualChild3 );
		actual.addChildren( containedComponents );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 3 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( difference.identifyingAttributesDifference.toString() )
				.isEqualTo( "expected id: comp1 expected path: comp1[1] expected type: "
						+ "de.retest.recheck.ui.diff.ElementBuilder$comp1 - "
						+ "actual id: comp1a actual path: comp1a[1]/comp1a[1] actual type: "
						+ "de.retest.recheck.ui.diff.ElementDifferenceFinderTest$comp1a" );
	}

	@Test
	void different_children_abc() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( null, comp1.class ) ),
				toAttributes( "{color=blue}" ) );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 3 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 4 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( expected.getContainedElements().size() ).isEqualTo( 3 );
		assertThat( actual.hasContainedElements() ).isFalse();
	}

	@Test
	void different_children_bc_abc() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( null, comp1.class ) ),
				toAttributes( "{color=blue}" ), null );

		final Element expectedChild2 = Element.create( "id", expected,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child2.class ) ),
				toAttributes( "{color=green}" ) );
		final Element expectedChild3 = Element.create( "id", expected,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child3.class ) ),
				toAttributes( "{color=violett}" ) );

		final List<Element> containedComponents = Arrays.asList( expectedChild2, expectedChild3 );
		expected.addChildren( containedComponents );

		final Element actual = buildElement();

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 2 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( expected.getContainedElements().size() ).isNotEqualTo( actual.getContainedElements().size() );
		assertThat( difference.toString() )
				.contains( "expected=null, actual=" + actual.getContainedElements().get( 0 ) );
	}

	@Test
	void different_children_abc_ab() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( null, comp1.class ) ),
				toAttributes( "{color=blue}" ), null );

		final Element actualChild1 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child1.class ) ),
				toAttributes( "{color=red}" ) );
		final Element actualChild2 = Element.create( "id", actual,
				new IdentifyingAttributes( createIdentifyingAttribute( fromString( "comp1" ), child2.class ) ),
				toAttributes( "{color=green}" ) );

		final List<Element> containedComponents = Arrays.asList( actualChild1, actualChild2 );
		actual.addChildren( containedComponents );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 2 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( expected.getContainedElements().size() ).isNotEqualTo( actual.getContainedElements().size() );
		assertThat( difference.toString() )
				.contains( "expected=" + expected.getContainedElements().get( 2 ) + ", actual=null" );
	}

	@Test
	void different_children_abc_null() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final ElementDifference difference = cut.differenceFor( expected, null );

		assertThat( expected ).isNotNull();
		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 1 );
		assertThat( difference.hasAnyDifference() ).isTrue();
		assertThat( difference.toString() ).contains( "expected=" + expected + ", actual=null" );
	}

	@Test
	void different_children_null_abc() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element actual = buildElement();

		final ElementDifference difference = cut.differenceFor( null, actual );

		assertThat( actual ).isNotNull();
		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 1 );
		assertThat( difference.hasAnyDifference() ).isTrue();
		assertThat( difference.toString() ).contains( "expected=null, actual=" + actual );

	}

	@Test
	void two_null_arguments_have_no_difference() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		assertThat( cut.differenceFor( null, null ) ).isNull();
	}

	@Test
	void expected_children_on_deeper_hierarchy_level() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element parent = mock( Element.class );
		final Attributes attributes = new Attributes();

		final Element element = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "root[0]/comp[0]/a[0]" ), child1.class ), attributes );

		final Element missing = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "root[0]/comp[0]" ), comp1.class ), attributes );
		missing.addChildren( element );

		final Element expected = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "root[0]" ), root.class ), attributes );
		expected.addChildren( missing );

		final Element otherElement = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "root[0]/a[0]" ), child1.class ), attributes );

		final Element actual = Element.create( "id", parent,
				IdentifyingAttributes.create( fromString( "root[0]" ), root.class ), attributes );
		actual.addChildren( otherElement );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.countAllContainedElements() ).isEqualTo( 2 );
		assertThat( expected.countAllContainedElements() ).isEqualTo( 3 );
		assertThat( expected.getContainedElements().contains( missing ) ).isTrue();
		assertThat( actual.getContainedElements().contains( missing ) ).isFalse();
		assertThat( difference.toString() )
				.contains( "expected=" + expected.getContainedElements().get( 0 ) + ", actual=null" );
	}

	@Test
	void actual_children_on_deeper_hierarchy_level() {
		final ElementDifferenceFinder cut = new ElementDifferenceFinder( mock( Environment.class ) );

		final Element expected = buildElement();

		final Element actual = Element.create( "id", mock( Element.class ),
				new IdentifyingAttributes( createIdentifyingAttribute( null, root.class ) ),
				toAttributes( "{color=red}" ), null );

		actual.addChildren( buildElement() );

		final ElementDifference difference = cut.differenceFor( expected, actual );

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.attributesDifference.toString() )
				.isEqualTo( "{color: expected=\"blue\", actual=\"red\"}" );
		assertThat( difference.identifyingAttributesDifference.toString() )
				.isEqualTo( "expected id: comp1 expected path: comp1[1] expected type: "
						+ "de.retest.recheck.ui.diff.ElementBuilder$comp1 - "
						+ "actual id: root actual path: root[1] actual type: "
						+ "de.retest.recheck.ui.diff.ElementDifferenceFinderTest$root" );
		assertThat( difference.hasChildDifferences() ).isTrue();
		assertThat( actual.getContainedElements() ).isEqualTo( Arrays.asList( expected ) );
		assertThat( actual.getContainedElements().get( 0 ).getContainedElements() )
				.isEqualTo( expected.getContainedElements() );
	}

	@Test
	void getNonEmptyDifferences_should_be_robust() {
		getNonEmptyDifferences( null );
		getNonEmptyDifferences( new ArrayList<>() );
	}

	@Test
	void getNonEmptyDifferences_should_handle_empty_element_differences() {
		final List<Difference> empty = new ArrayList<>();
		empty.add( new ElementDifference() );

		assertThat( getNonEmptyDifferences( empty ) ).hasSize( 0 );
	}

	@Test
	void getNonEmptyDifferences_should_ignore_attribute_differences() {
		final List<AttributeDifference> attributeDiff = new ArrayList<>();
		attributeDiff.add( new AttributeDifference( "text", "Mark", "Karl" ) );
		final AttributesDifference attributeDifference = new AttributesDifference( attributeDiff );

		assertThat( getNonEmptyDifferences( singletonList( attributeDifference ) ) ).hasSize( 0 );
	}

	@Test
	void getNonEmptyDifferences_should_return_nonempty_differences() throws Exception {
		final List<AttributeDifference> attributeDiff = new ArrayList<>();
		attributeDiff.add( new AttributeDifference( "text", "Mark", "Karl" ) );
		final AttributesDifference attributeDifference = new AttributesDifference( attributeDiff );
		final List<Difference> nonempty = new ArrayList<>();
		nonempty.add( new ElementDifference( mock( Element.class ), attributeDifference, null, null, null,
				new ArrayList<>() ) );

		assertThat( getNonEmptyDifferences( nonempty ) ).hasSize( 1 );
	}

	@Test
	void getElementDifferences_tracks_empty_differences() throws Exception {
		final List<Difference> empty = new ArrayList<>();
		empty.add( new ElementDifference() );

		assertThat( getElementDifferences( empty ) ).hasSize( 1 );
	}

	private static final class Type {}

	private static final class Comp {}

	private static class root {}

	private static class comp1a {}

	private static class otherChild1 {}

	private static class otherChild3 {}

}
