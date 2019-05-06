package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.diff.ElementDifferenceFinder.getNonEmptyDifferences;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;

class ElementDifferenceFinderTest {

	@Test
	void getNonEmptyDifferences_should_be_robust() {
		getNonEmptyDifferences( null );
		getNonEmptyDifferences( new ArrayList<>() );
	}

	@Test
	void getNonEmptyDifferences_should_handle_empty_element_diffs() {
		final List<Difference> empty = new ArrayList<>();
		empty.add( new ElementDifference() );
		assertThat( getNonEmptyDifferences( empty ) ).hasSize( 0 );
	}

	@Test
	void getNonEmptyDifferences_should_ignore_attribute_diffs() {
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

}
