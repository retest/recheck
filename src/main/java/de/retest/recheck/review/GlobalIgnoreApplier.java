package de.retest.recheck.review;

import static de.retest.recheck.util.Predicates.catchExceptionAsFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.ignore.RecheckIgnoreLocator;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.MatcherFilter;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class GlobalIgnoreApplier implements Filter {

	private final Counter counter;
	private final List<PersistentFilter> filtered = new ArrayList<>();
	private final RecheckIgnoreLocator locator;

	private GlobalIgnoreApplier( final Counter counter, final List<PersistentFilter> filtered ) {
		this( counter, filtered, new RecheckIgnoreLocator() );
	}

	private GlobalIgnoreApplier( final Counter counter, final List<PersistentFilter> filtered,
			final RecheckIgnoreLocator locator ) {
		this.counter = counter;
		this.filtered.addAll( filtered );
		this.locator = locator;
	}

	public static GlobalIgnoreApplier create( final Counter counter ) {
		return new GlobalIgnoreApplier( counter, Collections.emptyList() );
	}

	public static GlobalIgnoreApplier create( final Counter counter, final PersistableGlobalIgnoreApplier applier ) {
		return new GlobalIgnoreApplier( counter, applier.getIgnores() );
	}

	// Just for testing
	static GlobalIgnoreApplier create( final Counter counter, final RecheckIgnoreLocator locator ) {
		return new GlobalIgnoreApplier( counter, Collections.emptyList(), locator );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return any( filter -> filter.matches( element, attributeKey ) );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference difference ) {
		return any( filter -> filter.matches( element, difference ) );
	}

	public void ignoreAttribute( final Element element, final AttributeDifference difference ) {
		add( new AllMatchFilter( new MatcherFilter( new ElementRetestIdMatcher( element ) ),
				new AttributeFilter( difference.getKey() ) ) );
	}

	public void unignoreAttribute( final Element element, final AttributeDifference difference ) {
		remove( filter -> filter.matches( element, difference ) );
	}

	@Override
	public boolean matches( final Element element ) {
		return any( filter -> filter.matches( element ) );
	}

	@Override
	public boolean matches( final Element element, final ChangeType change ) {
		return any( filter -> filter.matches( element, change ) );
	}

	public void ignoreElement( final Element element ) {
		add( new MatcherFilter( new ElementRetestIdMatcher( element ) ) );
	}

	public void unignoreElement( final Element element ) {
		remove( filter -> filter.matches( element ) );
	}

	private void add( final Filter filter ) {
		// TODO Receive target path from GUI
		final Path ignorePath = locator.getProjectIgnoreFile().orElse( locator.getUserIgnoreFile() );
		filtered.add( new PersistentFilter( ignorePath, filter ) );
		counter.add();
	}

	public void addWithoutCounting( final PersistentFilter filter ) {
		filtered.add( filter );
	}

	private void remove( final Predicate<Filter> filter ) {
		filtered.removeIf( filter );
		counter.remove();
	}

	private boolean any( final Predicate<Filter> filter ) {
		return filtered.stream().anyMatch( catchExceptionAsFalse( filter ) );
	}

	public PersistableGlobalIgnoreApplier persist() {
		return new PersistableGlobalIgnoreApplier( filtered );
	}

	public static class PersistableGlobalIgnoreApplier {

		private final List<PersistentFilter> filters;

		public PersistableGlobalIgnoreApplier( final List<PersistentFilter> filters ) {
			this.filters = new ArrayList<>( filters );
		}

		public List<PersistentFilter> getIgnores() {
			return filters;
		}
	}
}
