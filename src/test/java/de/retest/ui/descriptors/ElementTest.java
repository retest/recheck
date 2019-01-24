package de.retest.ui.descriptors;

import static de.retest.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.review.ActionChangeSet;
import de.retest.util.RetestIdUtil.InvalidRetestIdException;

public class ElementTest {

	private static class Parent {}

	private final static RootElement rootElement = mock( RootElement.class );

	@Test
	public void toString_returns_UniqueCompIdentAttributes_toString() throws Exception {
		final IdentifyingAttributes compIdentAttributes = IdentifyingAttributes
				.create( fromString( "Window[1]/Path[1]/Component[1]" ), java.awt.Component.class );
		assertThat( Element.create( "asdef", rootElement, compIdentAttributes, new Attributes() ).toString() )
				.isEqualTo( compIdentAttributes.toString() );
		assertThat( compIdentAttributes.toString() ).isEqualTo( "Component" );
	}

	@Test
	public void applyChanges_to_path_propagates_to_child_components() {
		// Window[1]
		//   |- Parent[1]        = root
		//     |- Parent[1]      = parent0
		//       |- Component[1] = comp0
		//     |- Component[2]   = comp1
		//     |- Component[3]   = comp2
		final Element comp0 = createElement( "Window[1]/Parent[1]/Parent[1]/Component[1]", java.awt.Component.class );
		final Element parent0 = createElement( "Window[1]/Parent[1]/Parent[1]", java.awt.Component.class, comp0 );
		final Element comp1 = createElement( "Window[1]/Parent[1]/Component[2]", java.awt.Component.class );
		final Element comp2 = createElement( "Window[1]/Parent[1]/Component[3]", java.awt.Component.class );
		final Element root = createElement( "Window[1]/Parent[1]", Parent.class, parent0, comp1, comp2 );

		final ActionChangeSet changes = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		changes.getIdentAttributeChanges().add( root.getIdentifyingAttributes(), new AttributeDifference( "path",
				root.getIdentifyingAttributes().getPathTyped(), Path.fromString( "Window[1]/Parent[2]" ) ) );

		final Element newRoot = root.applyChanges( changes );
		final Element newComp2 = newRoot.getContainedElements().get( 2 );
		final Element newComp1 = newRoot.getContainedElements().get( 1 );
		final Element newParent0 = newRoot.getContainedElements().get( 0 );
		final Element newComp0 = newParent0.getContainedElements().get( 0 );

		assertThat( newRoot.getIdentifyingAttributes().getPath() ).isEqualTo( "Window[1]/Parent[2]" );
		assertThat( newComp2.getIdentifyingAttributes().getPath() ).isEqualTo( "Window[1]/Parent[2]/Component[3]" );
		assertThat( newComp1.getIdentifyingAttributes().getPath() ).isEqualTo( "Window[1]/Parent[2]/Component[2]" );
		assertThat( newParent0.getIdentifyingAttributes().getPath() ).isEqualTo( "Window[1]/Parent[2]/Parent[1]" );
		assertThat( newComp0.getIdentifyingAttributes().getPath() )
				.isEqualTo( "Window[1]/Parent[2]/Parent[1]/Component[1]" );
	}

	@Test
	public void applyChanges_should_add_inserted_components() throws Exception {
		final Element parent = createElement( "Parent[1]", java.awt.Component.class );
		final Element newChild = createElement( "Parent[1]/NewChild[1]", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();

		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( newChild );
	}

	@Test
	public void applyChanges_should_remove_deleted_components() throws Exception {
		final Element oldChild = createElement( "Parent[1]/NewChild[1]", java.awt.Component.class );
		final Element parent = createElement( "Parent[1]", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( oldChild.getIdentifyingAttributes() );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void parent_update_should_not_affect_insertion() {
		final Element parent = createElement( "ParentPathOld[1]", java.awt.Component.class );
		final Element newChild = createElement( "ParentPathNew[1]/NewChild[1]", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.getIdentAttributeChanges().add( parent.getIdentifyingAttributes(),
				new AttributeDifference( "path", fromString( "ParentPathOld[1]" ), fromString( "ParentPathNew[1]" ) ) );
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		assertThat( changed.identifyingAttributes.getPathTyped() ).isEqualTo( fromString( "ParentPathNew[1]" ) );
		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( newChild );
	}

	@Test
	public void no_insertion_match_should_not_change_anything() {
		final Element parent = createElement( "ParentPath[1]", java.awt.Component.class );
		final Element newChild = createElement( "NotParentPath[1]/NewChild[1]", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void applyChanges_should_work_in_complex_scenario() {
		final Element leaf0 = createElement( "Root[1]/Branch[1]/Leaf[1]", java.awt.Component.class );
		final Element leaf1 = createElement( "Root[1]/Branch[1]/Leaf[2]", java.awt.Component.class );
		final Element branch0 = createElement( "Root[1]/Branch[1]", java.awt.Component.class, leaf0, leaf1 );
		final Element leaf2 = createElement( "Root[1]/Branch[2]/Leaf[3]", java.awt.Component.class );
		final Element newLeaf3 = createElement( "Root[1]/Branch[2]/NewLeaf[4]", java.awt.Component.class );
		final Element branch1 = createElement( "Root[1]/Branch[2]", java.awt.Component.class, leaf2 );
		final Element root = createElement( "Root[1]", java.awt.Component.class, branch0, branch1 );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( leaf1.getIdentifyingAttributes() );
		actionChangeSet.addInsertChange( newLeaf3 );

		final Element changed = root.applyChanges( actionChangeSet );

		final List<Element> branches = changed.getContainedElements();
		assertThat( branches ).hasSize( 2 );
		assertThat( branches.get( 0 ).getContainedElements() ).containsExactly( leaf0 );
		assertThat( branches.get( 1 ).getContainedElements() ).containsExactly( leaf2, newLeaf3 );
	}

	@Test
	public void applyChanges_should_add_intermediate_elements() {
		// window
		final Element window = createElement( "Window[1]", java.awt.Component.class );

		// window/path_1/comp_1
		final Element element = createElement( "Window[1]/Path[1]/Comp[2]", java.awt.Component.class );
		final Element path = createElement( "Window[1]/Path[1]", java.awt.Component.class, element );

		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( path );
		actionChangeSet.addInsertChange( element );

		final Element changed = window.applyChanges( actionChangeSet );

		final List<Element> containedElements = changed.getContainedElements();
		assertThat( containedElements ).containsExactly( path );
		assertThat( containedElements.get( 0 ).getContainedElements() ).containsExactly( element );
	}

	@Test
	public void parent_update_should_not_affect_deletion() {
		final Path parentPathOld = Path.fromString( "ParentPathOld[1]" );
		final Path parentPathNew = Path.fromString( "ParentPathNew[1]" );
		final Element oldChild = createElement( "ParentPathOld[1]/NewChild[1]", java.awt.Component.class );
		final Element parent = createElement( "ParentPathOld[1]", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.getIdentAttributeChanges().add( parent.getIdentifyingAttributes(),
				new AttributeDifference( "path", parentPathOld, parentPathNew ) );
		actionChangeSet.addDeletedChange( oldChild.getIdentifyingAttributes() );

		final Element changed = parent.applyChanges( actionChangeSet );

		assertThat( changed.identifyingAttributes.getPathTyped() ).isEqualTo( parentPathNew );
		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void no_deletion_match_should_not_change_anything() {
		final Element oldChild = createElement( "ParentPath[1]/NewChild[1]", java.awt.Component.class );
		final Element parent = createElement( "ParentPath[1]", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( IdentifyingAttributes
				.create( Path.fromString( "NotParentPath[1]/NewChild[1]" ), java.awt.Component.class ) );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( oldChild );
	}

	@Test( expected = InvalidRetestIdException.class )
	public void null_id_should_throw_exception() {
		Element.create( null, rootElement, IdentifyingAttributes
				.create( Path.fromString( "NotParentPath[1]/NewChild[1]" ), java.awt.Component.class ),
				new MutableAttributes().immutable() );
	}

	@Test( expected = InvalidRetestIdException.class )
	public void empty_id_should_throw_exception() {
		Element.create( "", rootElement, IdentifyingAttributes
				.create( Path.fromString( "NotParentPath[1]/NewChild[1]" ), java.awt.Component.class ),
				new MutableAttributes().immutable() );
	}

	@Test( expected = InvalidRetestIdException.class )
	public void whitespace_in_id_should_throw_exception() {
		Element.create( " ", rootElement, IdentifyingAttributes
				.create( Path.fromString( "NotParentPath[1]/NewChild[1]" ), java.awt.Component.class ),
				new MutableAttributes().immutable() );
	}

	@Test( expected = InvalidRetestIdException.class )
	public void special_chars_in_id_should_throw_exception() {
		Element.create(
				"+(invalid]ID", rootElement, IdentifyingAttributes
						.create( Path.fromString( "NotParentPath[1]/NewChild[1]" ), java.awt.Component.class ),
				new MutableAttributes().immutable() );
	}

	@Test
	public void valid_UUID_should_be_allowed() {
		Element.create(
				UUID.randomUUID().toString(), rootElement, IdentifyingAttributes
						.create( Path.fromString( "NotParentPath[0]/NewChild[0]" ), java.awt.Component.class ),
				new MutableAttributes().immutable() );
	}

	@Test
	public void different_contained_elements_should_yield_differend_hash_code() throws Exception {
		final String retestId = "someRetestId";
		final IdentifyingAttributes identifyingAttributes =
				IdentifyingAttributes.create( Path.fromString( "SomePath[0]" ), "SomeType" );
		final Attributes attributes = new MutableAttributes().immutable();

		final Element e0 = Element.create( retestId, mock( Element.class ), identifyingAttributes, attributes );
		final Element e1 = Element.create( retestId, e0, identifyingAttributes, attributes );
		e1.addChildren( e0 );

		assertThat( e0 ).isNotEqualTo( e1 );
		assertThat( e0.hashCode() ).isNotEqualTo( e1.hashCode() );
	}

	// Copy & paste from ElementBuilder due to cyclic dependency.
	private static Element createElement( final String path, final Class<?> type,
			final Element... containedComponents ) {
		final Element element = Element.create( "asdas", rootElement,
				IdentifyingAttributes.create( fromString( path ), type ), new Attributes() );
		element.addChildren( containedComponents );
		return element;
	}

}
