package de.retest.recheck.ui.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;

class InsertedDeletedElementDifferenceTest {

	@Test
	void should_return_correct_element() {
		final Element element = mock( Element.class );
		final InsertedDeletedElementDifference inserted =
				InsertedDeletedElementDifference.differenceFor( null, element );
		final InsertedDeletedElementDifference deleted =
				InsertedDeletedElementDifference.differenceFor( element, null );
		assertThat( inserted.getInsertedOrDeletedElement() ).isEqualTo( element );
		assertThat( deleted.getInsertedOrDeletedElement() ).isEqualTo( element );
	}

}
