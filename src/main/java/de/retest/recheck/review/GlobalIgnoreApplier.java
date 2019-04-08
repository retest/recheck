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
	private final List<Filter> ignored = new ArrayList<>();

	private GlobalIgnoreApplier( final Counter counter, final List<Filter> ignored ) {
		this.counter = counter;
		this.ignored.addAll( ignored );
	}

	public static GlobalIgnoreApplier create( final Counter counter ) {
		return new GlobalIgnoreApplier( counter, Collections.emptyList() );
	}

	public static GlobalIgnoreApplier create( final Counter counter, final PersistableGlobalIgnoreApplier applier ) {
		return new GlobalIgnoreApplier( counter, applier.getIgnores() );
	}

	@Override
	public boolean filterAttributeDifference( final Element element, final AttributeDifference difference ) {
		return any( ignore -> ignore.filterAttributeDifference( element, difference ) );
	}

	public void ignoreAttribute( final Element element, final AttributeDifference difference ) {
		add( new ElementAttributeShouldIgnore( new ElementXPathMatcher( element ), difference.getKey() ) );
	}

	public void unignoreAttribute( final Element element, final AttributeDifference difference ) {
		remove( ignore -> ignore.filterAttributeDifference( element, difference ) );
	}

	@Override
	public boolean filterElement( final Element element ) {
		return any( ignore -> ignore.filterElement( element ) );
	}

	public void ignoreElement( final Element element ) {
		add( new ElementShouldIgnore( new ElementXPathMatcher( element ) ) );
	}

	public void unignoreElement( final Element element ) {
		remove( ignore -> ignore.filterElement( element ) );
	}

	public void add( final Filter ignore ) {
		ignored.add( ignore );
		counter.add();
	}

	private void remove( final Predicate<Filter> filter ) {
		ignored.removeIf( filter );
		counter.remove();
	}

	private boolean any( final Predicate<Filter> ignore ) {
		return ignored.stream().anyMatch( ignore );
	}

	public PersistableGlobalIgnoreApplier persist() {
		return new PersistableGlobalIgnoreApplier( ignored );
	}

	public static class PersistableGlobalIgnoreApplier {

		private final List<Filter> ignores;

		public PersistableGlobalIgnoreApplier( final List<Filter> ignores ) {
			this.ignores = new ArrayList<>( ignores );
		}

		public List<Filter> getIgnores() {
			return ignores;
		}
	}
}
