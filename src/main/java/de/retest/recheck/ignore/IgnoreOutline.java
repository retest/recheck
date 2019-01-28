package de.retest.recheck.ignore;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.OutlineAttribute;
import de.retest.ui.diff.AttributeDifference;

public class IgnoreOutline implements ShouldIgnore {

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		return OutlineAttribute.RELATIVE_OUTLINE.equalsIgnoreCase( attributeDifference.getKey() )
				|| OutlineAttribute.ABSOLUTE_OUTLINE.equalsIgnoreCase( attributeDifference.getKey() );
	}

}
