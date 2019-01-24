package de.retest.ui.review;

import static org.mockito.Mockito.mock;

import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.diff.AttributeDifference;

public final class ChangeSetTestUtils {

	private ChangeSetTestUtils() {}

	static void fillSuiteChangeSet( final SuiteChangeSet suiteChangeSet ) {
		fillTestChangeSet( suiteChangeSet.createTestChangeSet() );
	}

	static void fillTestChangeSet( final TestChangeSet testChangeSet ) {
		fillActionChangeSet( testChangeSet.createActionChangeSet() );
	}

	static void fillActionChangeSet( final ActionChangeSet actionChangeSet ) {
		actionChangeSet.getIdentAttributeChanges().add( mock( IdentifyingAttributes.class ),
				mock( AttributeDifference.class ) );
	}

}
