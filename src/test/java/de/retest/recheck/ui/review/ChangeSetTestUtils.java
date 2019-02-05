package de.retest.recheck.ui.review;

import static org.mockito.Mockito.mock;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

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
