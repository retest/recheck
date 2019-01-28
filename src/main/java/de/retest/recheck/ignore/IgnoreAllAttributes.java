package de.retest.recheck.ignore;

import de.retest.ui.descriptors.Element;
import de.retest.ui.diff.AttributeDifference;

public class IgnoreAllAttributes implements ShouldIgnore {

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		return true;
	}

}
