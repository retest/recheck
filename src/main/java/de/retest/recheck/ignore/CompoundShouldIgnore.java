package de.retest.recheck.ignore;

import java.util.List;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class CompoundShouldIgnore implements Filter {

	private final List<Filter> filters;

	public CompoundShouldIgnore( final List<Filter> filters ) {
		this.filters = filters;
	}

	@Override
	public boolean shouldBeFiltered( final Element element ) {
		for ( final Filter filter : filters ) {
			if ( filter.shouldBeFiltered( element ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldBeFiltered( final Element element, final AttributeDifference attributeDifference ) {
		for ( final Filter filter : filters ) {
			if ( filter.shouldBeFiltered( element, attributeDifference ) ) {
				return true;
			}
		}
		return false;
	}

}
