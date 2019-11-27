package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.OutlineAttribute;

public class FilterOutline implements Filter {

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return OutlineAttribute.RELATIVE_OUTLINE.equalsIgnoreCase( attributeKey )
				|| OutlineAttribute.ABSOLUTE_OUTLINE.equalsIgnoreCase( attributeKey );
	}

}
