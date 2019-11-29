package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.review.ActionChangeSet;

class SutStateTest {

	@Test
	void applyChanges_should_change_metadata() {
		final HashMap<String, String> oldMetadata = new HashMap<>();
		oldMetadata.put( "unchanged", "oldValue" );
		oldMetadata.put( "updated", "oldValue" );
		final SutState old = new SutState( Collections.emptyList(), oldMetadata );

		final HashMap<String, String> newMetadata = new HashMap<>();
		newMetadata.put( "updated", "newValue" );
		final ActionChangeSet update = new ActionChangeSet( "update", "", null, newMetadata );

		final SutState updated = old.applyChanges( update );

		assertThat( updated.getMetadata( "unchanged" ) ).isEqualTo( "oldValue" );
		assertThat( updated.getMetadata( "updated" ) ).isEqualTo( "newValue" );
	}

}
