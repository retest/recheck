package de.retest.recheck.ui.descriptors;

import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.ScreenshotChanges;

public final class ActionChangeSetTestUtils {

	private ActionChangeSetTestUtils() {}

	static ActionChangeSet createEmptyActionChangeSet() {
		return new ActionChangeSet( null, null, ScreenshotChanges.empty() );
	}

}
