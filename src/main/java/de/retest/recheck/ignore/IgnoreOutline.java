package de.retest.recheck.ignore;

import java.io.Serializable;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.OutlineAttribute;

public class IgnoreOutline implements ShouldIgnore {

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreElement( final IdentifyingAttributes identifyingAttributes ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final IdentifyingAttributes comp, final String key,
			final Serializable expectedValue, final Serializable actualValue ) {
		return OutlineAttribute.RELATIVE_OUTLINE.equalsIgnoreCase( key )
				|| OutlineAttribute.ABSOLUTE_OUTLINE.equalsIgnoreCase( key );
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final String elementRetestId, final String key,
			final Serializable expectedValue, final Serializable actualValue ) {
		return OutlineAttribute.RELATIVE_OUTLINE.equalsIgnoreCase( key )
				|| OutlineAttribute.ABSOLUTE_OUTLINE.equalsIgnoreCase( key );
	}

}
