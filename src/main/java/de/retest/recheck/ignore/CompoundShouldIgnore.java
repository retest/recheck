package de.retest.recheck.ignore;

import java.io.Serializable;
import java.util.List;

import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;

public class CompoundShouldIgnore implements ShouldIgnore {

	private final List<ShouldIgnore> shouldIgnores;

	public CompoundShouldIgnore( final List<ShouldIgnore> shouldIgnores ) {
		this.shouldIgnores = shouldIgnores;
	}

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		for ( final ShouldIgnore shouldIgnore : shouldIgnores ) {
			if ( shouldIgnore.shouldIgnoreElement( element ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldIgnoreElement( final IdentifyingAttributes identifyingAttributes ) {
		for ( final ShouldIgnore shouldIgnore : shouldIgnores ) {
			if ( shouldIgnore.shouldIgnoreElement( identifyingAttributes ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final IdentifyingAttributes comp, final String key,
			final Serializable expectedValue, final Serializable actualValue ) {
		for ( final ShouldIgnore shouldIgnore : shouldIgnores ) {
			if ( shouldIgnore.shouldIgnoreAttributeDifference( comp, key, expectedValue, actualValue ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final String elementRetestId, final String key,
			final Serializable expectedValue, final Serializable actualValue ) {
		for ( final ShouldIgnore shouldIgnore : shouldIgnores ) {
			if ( shouldIgnore.shouldIgnoreAttributeDifference( elementRetestId, key, expectedValue, actualValue ) ) {
				return true;
			}
		}
		return false;
	}

}
