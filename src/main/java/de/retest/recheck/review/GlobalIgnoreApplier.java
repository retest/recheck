package de.retest.recheck.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.ElementAttributeFilter;
import de.retest.recheck.review.ignore.ElementFilter;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class GlobalIgnoreApplier implements Filter {

	private final Counter counter;
	private final List<Filter> filtered = new ArrayList<>();

	private GlobalIgnoreApplier( final Counter counter, final List<Filter> filtered ) {
		this.counter = counter;
		this.filtered.addAll( filtered );
	}

	public static GlobalIgnoreApplier create( final Counter counter ) {
		return new GlobalIgnoreApplier( counter, Collections.emptyList() );
	}

	public static GlobalIgnoreApplier create( final Counter counter, final PersistableGlobalIgnoreApplier applier ) {
		return new GlobalIgnoreApplier( counter, applier.getIgnores() );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference difference ) {
		return any( filter -> filter.matches( element, difference ) );
	}

	public void ignoreAttribute( final Element element, final AttributeDifference difference ) {
		add( new ElementAttributeFilter( new ElementXPathMatcher( element ), difference.getKey() ) );
	}

	public void unignoreAttribute( final Element element, final AttributeDifference difference ) {
		remove( filter -> filter.matches( element, difference ) );
	}

	@Override
	public boolean matches( final Element element ) {
		return any( filter -> filter.matches( element ) );
	}

	public void ignoreElement( final Element element ) {
		add( new ElementFilter( new ElementXPathMatcher( element ) ) );
	}

	public void unignoreElement( final Element element ) {
		remove( filter -> filter.matches( element ) );
	}

	private void add( final Filter filter ) {
		filtered.add( filter );
		counter.add();
	}

	public void addJSRuleFile( final Filter filter ) {
		filtered.add( filter );
	}

	private void remove( final Predicate<Filter> filter ) {
		filtered.removeIf( filter );
		counter.remove();
	}

	private boolean any( final Predicate<Filter> filter ) {
		return filtered.stream().anyMatch( filter );
	}

	public PersistableGlobalIgnoreApplier persist() {
		return new PersistableGlobalIgnoreApplier( filtered );
	}

	public static class PersistableGlobalIgnoreApplier {

		private final List<Filter> filters;

		public PersistableGlobalIgnoreApplier( final List<Filter> filters ) {
			this.filters = new ArrayList<>( filters );
		}

		public List<Filter> getIgnores() {
			return filters;
		}
	}
}
