package de.retest.recheck.ignore;

import java.util.List;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class CompoundFilter implements Filter {

	private final List<Filter> filters;

	public CompoundFilter( final List<Filter> filters ) {
		this.filters = filters;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	public boolean matches( final Element element ) {
		for ( final Filter filter : filters ) {
			if ( filter.matches( element ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		for ( final Filter filter : filters ) {
			if ( filter.matches( element, attributeDifference ) ) {
				return true;
			}
		}
		return false;
	}

}
