package de.retest.recheck.ignore;

import static de.retest.recheck.util.Predicates.catchExceptionAsFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;
import lombok.ToString;

/**
 * A compound filter consists of multiple filters that are combined into one via an OR. So the compound filter returns
 * true if _any_ of its filters return true.
 */
@ToString
public class CompoundFilter implements Filter {

	@Getter
	private final List<Filter> filters;

	public CompoundFilter() {
		this( Collections.emptyList() );
	}

	public CompoundFilter( final Filter... filters ) {
		this( Arrays.asList( filters ) );
	}

	public CompoundFilter( final List<Filter> filters ) {
		this.filters = filters;
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
	public boolean matches( final Element element, final ChangeType change ) {
		return filters.stream().anyMatch( catchExceptionAsFalse( f -> f.matches( element, change ) ) );
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

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		for ( final Filter filter : filters ) {
			if ( filter.matches( element, attributeKey ) ) {
				return true;
			}
		}
		return false;
	}
}
