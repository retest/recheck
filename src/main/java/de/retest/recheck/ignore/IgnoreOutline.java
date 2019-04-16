package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.diff.AttributeDifference;

public class IgnoreOutline implements Filter {

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element,
			final AttributeDifference attributeDifference ) {
		return OutlineAttribute.RELATIVE_OUTLINE.equalsIgnoreCase( attributeDifference.getKey() )
				|| OutlineAttribute.ABSOLUTE_OUTLINE.equalsIgnoreCase( attributeDifference.getKey() );
	}

}
