package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;

class ElementDifferenceTest {

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

		final ElementDifference e1 = new ElementDifference( element, null, null, null, null, childDifferences );
		final ElementDifference e2 = new ElementDifference( element, attributes, null, null, null, childDifferences );
		final ElementDifference e3 = new ElementDifference( element, null, identifying, null, null, childDifferences );
		final ElementDifference e4 =
				new ElementDifference( element, attributes, identifying, null, null, childDifferences );
		final ElementDifference e5 = new ElementDifference( element, null, insertion, null, null, childDifferences );
		final ElementDifference e6 = new ElementDifference( element, null, deletion, null, null, childDifferences );

		assertThat( e1.hasAnyDifference() ).isFalse();
		assertThat( e2.hasAnyDifference() ).isTrue();
		assertThat( e3.hasAnyDifference() ).isTrue();
		assertThat( e4.hasAnyDifference() ).isTrue();
		assertThat( e5.hasAnyDifference() ).isTrue();
		assertThat( e6.hasAnyDifference() ).isTrue();
	}
}
