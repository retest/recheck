package de.retest.recheck.ignore;

import java.util.List;

import de.retest.ui.descriptors.Element;
import de.retest.ui.diff.AttributeDifference;

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
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		for ( final ShouldIgnore shouldIgnore : shouldIgnores ) {
			if ( shouldIgnore.shouldIgnoreAttributeDifference( element, attributeDifference ) ) {
				return true;
			}
		}
		return false;
	}

}
