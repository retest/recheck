package de.retest.recheck.ignore;

import java.io.Serializable;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;

public class IgnoreAllAttributes implements ShouldIgnore {

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
		return true;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final String elementRetestId, final String key,
			final Serializable expectedValue, final Serializable actualValue ) {
		return true;
	}

}
