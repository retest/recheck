package de.retest.recheck.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.ElementAttributeShouldIgnore;
import de.retest.recheck.review.ignore.ElementShouldIgnore;
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
	public boolean shouldBeFiltered( final Element element, final AttributeDifference difference ) {
		return any( filter -> filter.shouldBeFiltered( element, difference ) );
	}

	public void ignoreAttribute( final Element element, final AttributeDifference difference ) {
		add( new ElementAttributeShouldIgnore( new ElementXPathMatcher( element ), difference.getKey() ) );
	}

	public void unignoreAttribute( final Element element, final AttributeDifference difference ) {
		remove( filter -> filter.shouldBeFiltered( element, difference ) );
	}

	@Override
	public boolean shouldBeFiltered( final Element element ) {
		return any( filter -> filter.shouldBeFiltered( element ) );
	}

	public void ignoreElement( final Element element ) {
		add( new ElementShouldIgnore( new ElementXPathMatcher( element ) ) );
	}

	public void unignoreElement( final Element element ) {
		remove( filter -> filter.shouldBeFiltered( element ) );
	}

	public void add( final Filter filter ) {
		filtered.add( filter );
		counter.add();
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
