package de.retest.recheck.ui.diff;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.image.Screenshot;

class ElementDifferenceTest {

	@Test
	void getCopyWithFlattenedChildDifferenceHierarchy_should_iterate_over_elementDifferences_without_attributes()
			throws Exception {
		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		final Collection<ElementDifference> childDifferences = new ArrayList<>();
		final IdentifyingAttributesDifference identifyingAttributesDifference =
				mock( IdentifyingAttributesDifference.class );
		final AttributesDifference attributesDifference = mock( AttributesDifference.class );

		final ElementDifference childCut = new ElementDifference( element, attributesDifference,
				identifyingAttributesDifference, null, null, childDifferences );
		final ElementDifference parentCut =
				new ElementDifference( element, null, null, null, null, Arrays.asList( childCut ) );
		final ElementDifference grandparentCut =
				new ElementDifference( element, null, null, null, null, Arrays.asList( parentCut ) );

		assertThat( ElementDifference.getCopyWithFlattenedChildDifferenceHierarchy( grandparentCut ) )
				.isEqualTo( childCut );
	}

	@Test
	void getImmediateDifferences_should_gather_differences() {
		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );

		final ElementDifference cut1 = new ElementDifference( mock( Element.class ), attributesDifference, idDifference,
				null, null, Collections.emptyList() );
		final ElementDifference cut2 =
				new ElementDifference( mock( Element.class ), null, idDifference, null, null, Collections.emptyList() );
		final ElementDifference cut3 = new ElementDifference( mock( Element.class ), attributesDifference, null, null,
				null, Collections.emptyList() );
		final ElementDifference cut4 =
				new ElementDifference( mock( Element.class ), null, null, null, null, Collections.emptyList() );

		assertThat( cut1.getImmediateDifferences() ).hasSize( 2 );
		assertThat( cut2.getImmediateDifferences() ).hasSize( 1 );
		assertThat( cut3.getImmediateDifferences() ).hasSize( 1 );
		assertThat( cut4.getImmediateDifferences() ).hasSize( 0 );
	}

	@Test
	void getAttributeDifferences_should_gather_all_differences() {
		final AttributeDifference difference = mock( AttributeDifference.class );

		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		when( attributesDifference.getDifferences() ).thenReturn( Collections.nCopies( 5, difference ) );

		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );
		when( idDifference.getAttributeDifferences() ).thenReturn( Collections.nCopies( 3, difference ) );

		final ElementDifference cut = new ElementDifference( mock( Element.class ), attributesDifference, idDifference,
				null, null, Collections.emptyList() );

		assertThat( cut.getAttributeDifferences() ).hasSize( 8 );
	}

	@Test
	void getAttributeDifferences_should_gather_all_differences_from_identifying() {
		final AttributeDifference difference = mock( AttributeDifference.class );

		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );
		when( idDifference.getAttributeDifferences() ).thenReturn( Collections.nCopies( 3, difference ) );

		final ElementDifference cut =
				new ElementDifference( mock( Element.class ), null, idDifference, null, null, Collections.emptyList() );

		assertThat( cut.getAttributeDifferences() ).hasSize( 3 );
	}

	@Test
	void getAttributeDifferences_should_gather_all_differences_from_attributes() {
		final AttributeDifference difference = mock( AttributeDifference.class );

		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		when( attributesDifference.getDifferences() ).thenReturn( Collections.nCopies( 5, difference ) );

		final ElementDifference cut = new ElementDifference( mock( Element.class ), attributesDifference, null, null,
				null, Collections.emptyList() );

		assertThat( cut.getAttributeDifferences() ).hasSize( 5 );
	}

	@Test
	void getAttributeDifferences_should_gather_all_differences_should_not_use_child_differences() {
		final AttributeDifference difference = mock( AttributeDifference.class );

		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		when( attributesDifference.getDifferences() ).thenReturn( Collections.nCopies( 5, difference ) );

		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );
		when( idDifference.getAttributeDifferences() ).thenReturn( Collections.nCopies( 3, difference ) );

		final ElementDifference childDifference = new ElementDifference( mock( Element.class ), attributesDifference,
				idDifference, null, null, Collections.emptyList() );

		final ElementDifference cut = new ElementDifference( mock( Element.class ), null, null, null, null,
				Collections.nCopies( 5, childDifference ) );

		assertThat( cut.getAttributeDifferences() ).isEmpty();
	}

	@Test
	void getIdentifier_returns_different_strings_for_different_compositions() throws Exception {
		final Element element = mock( Element.class );

		final IdentifyingAttributes idAttributes = mock( IdentifyingAttributes.class );
		when( element.getIdentifyingAttributes() ).thenReturn( idAttributes );
		when( idAttributes.identifier() ).thenReturn( "kp" );

		final IdentifyingAttributesDifference idAttributesDiff = mock( IdentifyingAttributesDifference.class );

		final AttributesDifference attributesDifference = mock( AttributesDifference.class );

		final ElementDifference cut1 =
				new ElementDifference( element, null, null, null, null, Collections.emptyList() );
		final ElementDifference cut2 =
				new ElementDifference( element, attributesDifference, null, null, null, Collections.emptyList() );
		final ElementDifference cut3 =
				new ElementDifference( element, null, idAttributesDiff, null, null, Collections.emptyList() );
		final ElementDifference cut4 = new ElementDifference( element, attributesDifference, idAttributesDiff, null,
				null, Collections.emptyList() );

		assertThat( cut1.getIdentifier() )
				.isEqualTo( "5147a6de6c26ca2472e0b20e803a1ba7fe6643775294aa714b6263bf176c37d1" );
		assertThat( cut2.getIdentifier() )
				.isEqualTo( "fa318aa5a0a2f9541be1381b59e8dc643992b67b2ff16775967edaa2db16a4ea" );
		assertThat( cut3.getIdentifier() )
				.isEqualTo( "5787f3af5816d607694eda8f1a90b77068d17b49bd6474429957d5dced0ebf6f" );
		assertThat( cut4.getIdentifier() )
				.isEqualTo( "9ddd1cf5c933887fc18f4f7ba97547d48056ac3d442bc64400fb7b7bab70a71c" );
	}

	@Test
	void hasAttributesDifferences_should_return_false_if_difference_empty() {
		final AttributesDifference emptyAttributes = mock( AttributesDifference.class );
		when( emptyAttributes.getDifferences() ).thenReturn( Collections.emptyList() );
		when( emptyAttributes.size() ).thenReturn( 0 );

		final Element element = mock( Element.class );
		final List<ElementDifference> childDifferences = Collections.emptyList();
		final ElementDifference cut =
				new ElementDifference( element, emptyAttributes, null, null, null, childDifferences );

		assertThat( cut.hasAttributesDifferences() ).isFalse();
	}

	@Test
	void hasAttributesDifferences_should_return_false_if_difference_null() {
		final Element element = mock( Element.class );
		final List<ElementDifference> childDifferences = Collections.emptyList();
		final ElementDifference cut = new ElementDifference( element, null, null, null, null, childDifferences );

		assertThat( cut.hasAttributesDifferences() ).isFalse();
	}

	@Test
	void hasAttributesDifferences_should_return_true_if_difference_contained() {
		final AttributeDifference attributeDifference = mock( AttributeDifference.class );

		final AttributesDifference attributes = mock( AttributesDifference.class );
		when( attributes.getDifferences() ).thenReturn( Collections.singletonList( attributeDifference ) );
		when( attributes.size() ).thenReturn( 1 );

		final Element element = mock( Element.class );
		final List<ElementDifference> childDifferences = Collections.emptyList();
		final ElementDifference cut = new ElementDifference( element, null, null, null, null, childDifferences );

		assertThat( cut.hasAttributesDifferences() ).isFalse();
	}

	@Test
	void isInsertion_should_return_true_if_insertion() throws Exception {
		final ElementDifferenceFinder elementDifferenceFinder =
				new ElementDifferenceFinder( mock( Environment.class ) );
		final ElementDifference cut = elementDifferenceFinder.differenceFor( null, mock( Element.class ) );

		assertThat( cut.isInsertionOrDeletion() ).isTrue();
		assertThat( cut.isDeletion() ).isFalse();
		assertThat( cut.isInsertion() ).isTrue();
	}

	@Test
	void isDeletion_should_return_true_if_deletion() throws Exception {
		final ElementDifferenceFinder elementDifferenceFinder =
				new ElementDifferenceFinder( mock( Environment.class ) );
		final ElementDifference cut = elementDifferenceFinder.differenceFor( mock( Element.class ), null );

		assertThat( cut.isInsertionOrDeletion() ).isTrue();
		assertThat( cut.isInsertion() ).isFalse();
		assertThat( cut.isDeletion() ).isTrue();
	}

	@Test
	void hasAnyDifference_should_respect_all_possible_difference_representations() {
		final Element element = mock( Element.class );
		final List<ElementDifference> childDifferences = Collections.emptyList();

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		final AttributesDifference attributes = mock( AttributesDifference.class );
		when( attributes.getDifferences() ).thenReturn( Collections.singletonList( attributeDifference ) );
		when( attributes.size() ).thenReturn( 1 );
		final IdentifyingAttributesDifference identifying = mock( IdentifyingAttributesDifference.class );
		final InsertedDeletedElementDifference insertion = mock( InsertedDeletedElementDifference.class );
		when( insertion.isInserted() ).thenReturn( true );
		final InsertedDeletedElementDifference deletion = mock( InsertedDeletedElementDifference.class );
		when( deletion.isInserted() ).thenReturn( false );

		final ElementDifference cut1 = new ElementDifference( element, null, null, null, null, childDifferences );
		final ElementDifference cut2 = new ElementDifference( element, attributes, null, null, null, childDifferences );
		final ElementDifference cut3 = new ElementDifference( element, null, identifying, null, null, childDifferences );
		final ElementDifference cut4 =
				new ElementDifference( element, attributes, identifying, null, null, childDifferences );
		final ElementDifference cut5 = new ElementDifference( element, null, insertion, null, null, childDifferences );
		final ElementDifference cut6 = new ElementDifference( element, null, deletion, null, null, childDifferences );

		assertThat( cut1.hasAnyDifference() ).isFalse();
		assertThat( cut2.hasAnyDifference() ).isTrue();
		assertThat( cut3.hasAnyDifference() ).isTrue();
		assertThat( cut4.hasAnyDifference() ).isTrue();
		assertThat( cut5.hasAnyDifference() ).isTrue();
		assertThat( cut6.hasAnyDifference() ).isTrue();
	}

	@Test
	void hasAnyDifference_should_exclude_child_differences() {
		final AttributesDifference attributesDiff = mock( AttributesDifference.class );
		when( attributesDiff.size() ).thenReturn( 1 );

		final Element element = mock( Element.class );

		final ElementDifference childWithOwnDiffs =
				new ElementDifference( element, attributesDiff, null, null, null, Collections.emptyList() );

		assertThat( childWithOwnDiffs.hasAnyDifference() ).isTrue();
		assertThat( childWithOwnDiffs.hasChildDifferences() ).isFalse();

		final ElementDifference parentWithoutOwnDiffs = new ElementDifference( element, null, null, null, null,
				Collections.singletonList( childWithOwnDiffs ) );

		assertThat( parentWithoutOwnDiffs.hasAnyDifference() ).isFalse();
		assertThat( parentWithoutOwnDiffs.hasChildDifferences() ).isTrue();
	}

	@Test
	void size_returns_zero_if_elements_are_null() throws Exception {
		final ElementDifference cut = new ElementDifference();

		assertThat( cut.size() ).isEqualTo( 0 );
	}

	@Test
	void size_returns_one_if_attributes_are_not_null() throws Exception {
		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );

		final ElementDifference cut = new ElementDifference( mock( Element.class ), attributesDifference, idDifference,
				null, null, Collections.emptyList() );

		assertThat( cut.size() ).isEqualTo( 1 );
	}

	@Test
	void size_counts_all_child_differences() throws Exception {
		final Element element = mock( Element.class );
		final AttributeDifference difference = mock( AttributeDifference.class );

		final AttributesDifference attributesDifference = mock( AttributesDifference.class );
		when( attributesDifference.getDifferences() ).thenReturn( Collections.nCopies( 5, difference ) );

		final IdentifyingAttributesDifference idDifference = mock( IdentifyingAttributesDifference.class );
		when( idDifference.getAttributeDifferences() ).thenReturn( Collections.nCopies( 3, difference ) );

		final ElementDifference childDifference = new ElementDifference( mock( Element.class ), attributesDifference,
				idDifference, null, null, Collections.emptyList() );

		final ElementDifference cut =
				new ElementDifference( element, null, null, null, null, Collections.nCopies( 5, childDifference ) );

		assertThat( cut.size() ).isEqualTo( 5 );
	}

	@Test
	void getNonEmptyDifferences_should_add_ElementDifference_if_identifyingAttributesDifference_is_present()
			throws Exception {
		final Element element = mock( Element.class );
		final Collection<ElementDifference> childDifferences = new ArrayList<>();
		final Screenshot actualScreenshot = mock( Screenshot.class );
		final Screenshot expectedScreenshot = mock( Screenshot.class );
		final LeafDifference identifyingAttributesDifference = mock( LeafDifference.class );
		final AttributesDifference attributesDifference = null;
		final ElementDifference cut = new ElementDifference( element, attributesDifference,
				identifyingAttributesDifference, expectedScreenshot, actualScreenshot, childDifferences );

		assertThat( cut.getNonEmptyDifferences() ).containsExactly( cut );
	}

	@Test
	void getNonEmptyDifferences_should_add_ElementDifference_if_AttributesDifference_is_present() throws Exception {
		final Element element = mock( Element.class );
		final Collection<ElementDifference> childDifferences = new ArrayList<>();
		final Screenshot actualScreenshot = mock( Screenshot.class );
		final Screenshot expectedScreenshot = mock( Screenshot.class );
		final LeafDifference identifyingAttributesDifference = null;
		final AttributesDifference attributesDifference = mock( AttributesDifference.class );

		final ElementDifference cut = new ElementDifference( element, attributesDifference,
				identifyingAttributesDifference, expectedScreenshot, actualScreenshot, childDifferences );

		assertThat( cut.getNonEmptyDifferences() ).containsExactly( cut );
	}

	@Test
	void getNonEmptyDifferences_should_add_child_differences_if_there_are_any() throws Exception {
		final Element element = mock( Element.class );
		final Collection<ElementDifference> childDifferences = new ArrayList<>();
		final Screenshot actualScreenshot = mock( Screenshot.class );
		final Screenshot expectedScreenshot = mock( Screenshot.class );
		final LeafDifference identifyingAttributesDifference = null;
		final AttributesDifference attributesDifference = null;

		final ElementDifference childCut = new ElementDifference( element, mock( AttributesDifference.class ),
				identifyingAttributesDifference, expectedScreenshot, actualScreenshot, childDifferences );
		final ElementDifference parentCut = new ElementDifference( element, attributesDifference,
				mock( LeafDifference.class ), expectedScreenshot, actualScreenshot, Arrays.asList( childCut ) );
		final ElementDifference grandparentCut = new ElementDifference( element, attributesDifference,
				identifyingAttributesDifference, expectedScreenshot, actualScreenshot, Arrays.asList( parentCut ) );

		assertThat( grandparentCut.getNonEmptyDifferences() ).containsExactly( parentCut, childCut );
	}

	@Test
	void getNonEmptyDifferences_should_not_return_empty_differences() throws Exception {
		final ElementDifference cut = new ElementDifference();

		assertThat( cut.getNonEmptyDifferences() ).isEmpty();
	}

	@Test
	void getElementDifferences_should_include_all_child_differences() throws Exception {
		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		final ElementDifference childDifference =
				new ElementDifference( element, null, null, null, null, Collections.emptyList() );

		final ElementDifference cut =
				new ElementDifference( element, null, null, null, null, Collections.nCopies( 5, childDifference ) );

		assertThat( cut.getElementDifferences() ).hasSize( 6 );
	}

	@Test
	void compareTo_returns_correct_int() throws Exception {
		final ElementDifference cut = mock( ElementDifference.class );
		final ElementDifference otherCut = mock( ElementDifference.class );

		assertThat( cut.compareTo( cut ) ).isEqualTo( 0 );
		assertThat( cut.compareTo( otherCut ) ).isEqualTo( 1 );
	}

	@Test
	void child_differences_should_be_sorted() {
		final List<ElementDifference> childDifferences = new ArrayList<>();
		final ElementDifference div2 = new ElementDifference(
				Element.create( "div2", mock( Element.class ),
						IdentifyingAttributes.create( Path.fromString( "html[1]/div[2]" ), "div" ),
						new MutableAttributes().immutable() ),
				mock( AttributesDifference.class ), null, null, null, emptyList() );
		childDifferences.add( div2 );
		final ElementDifference div1 = new ElementDifference(
				Element.create( "div1", mock( Element.class ),
						IdentifyingAttributes.create( Path.fromString( "html[1]/div[1]" ), "div" ),
						new MutableAttributes().immutable() ),
				mock( AttributesDifference.class ), null, null, null, emptyList() );
		childDifferences.add( div1 );

		final ElementDifference cut = new ElementDifference( mock( Element.class ), mock( AttributesDifference.class ),
				mock( IdentifyingAttributesDifference.class ), null, null, childDifferences );

		assertThat( cut.getChildDifferences().get( 0 ) ).isEqualTo( div1 );
		assertThat( cut.getChildDifferences().get( 1 ) ).isEqualTo( div2 );
	}
}
