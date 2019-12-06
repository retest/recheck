package de.retest.recheck.ui.diff.meta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class MetadataDifferenceTest {

	@Test
	void constructor_should_not_allow_duplicate_keys() throws Exception {
		final MetadataDifference cut = MetadataDifference.of( of( //
				new MetadataElementDifference( "a", "b", "c" ), //
				new MetadataElementDifference( "a", "e", "f" ) //
		) );

		assertThat( cut ).hasSize( 1 );
		assertThat( cut ).containsOnlyOnce( new MetadataElementDifference( "a", "e", "f" ) );
	}

	@Test
	void constructor_should_allow_unique_keys() throws Exception {
		final MetadataDifference cut = MetadataDifference.of( of( //
				new MetadataElementDifference( "a", "b", "c" ), //
				new MetadataElementDifference( "b", "e", "f" ) //
		) );

		assertThat( cut ).hasSize( 2 );
	}

	private Set<MetadataElementDifference> of( final MetadataElementDifference... differences ) {
		return new HashSet<>( Arrays.asList( differences ) );
	}
}
