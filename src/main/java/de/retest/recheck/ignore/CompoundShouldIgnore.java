package de.retest.recheck.ignore;

import java.util.List;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class CompoundShouldIgnore implements Filter {

	private final List<Filter> filter;

	public CompoundShouldIgnore( final List<Filter> shouldIgnores ) {
		filter = shouldIgnores;
	}

	@Override
	public boolean filterElement( final Element element ) {
		for ( final Filter filter : filter ) {
			if ( filter.filterElement( element ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean filterAttributeDifference( final Element element, final AttributeDifference attributeDifference ) {
		for ( final Filter filter : filter ) {
			if ( filter.filterAttributeDifference( element, attributeDifference ) ) {
				return true;
			}
		}
		return false;
	}

}
