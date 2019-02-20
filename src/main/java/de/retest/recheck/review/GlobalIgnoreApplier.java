package de.retest.recheck.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.ignore.ElementAttributeShouldIgnore;
import de.retest.recheck.review.ignore.ElementShouldIgnore;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class GlobalIgnoreApplier implements ShouldIgnore {

	private final Counter counter;
	private final List<ShouldIgnore> ignored = new ArrayList<>();

	private GlobalIgnoreApplier( final Counter counter, final List<ShouldIgnore> ignored ) {
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
	public boolean shouldIgnoreAttributeDifference( final Element element, final AttributeDifference difference ) {
		return any( ignore -> ignore.shouldIgnoreAttributeDifference( element, difference ) );
	}

	public void ignoreAttribute( final Element element, final AttributeDifference difference ) {
		add( new ElementAttributeShouldIgnore( new ElementIdMatcher( element ), difference.getKey() ) );
	}

	public void unignoreAttribute( final Element element, final AttributeDifference difference ) {
		remove( ignore -> ignore.shouldIgnoreAttributeDifference( element, difference ) );
	}

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return any( ignore -> ignore.shouldIgnoreElement( element ) );
	}

	public void ignoreElement( final Element element ) {
		add( new ElementShouldIgnore( new ElementIdMatcher( element ) ) );
	}

	public void unignoreElement( final Element element ) {
		remove( ignore -> ignore.shouldIgnoreElement( element ) );
	}

	public void add( final ShouldIgnore ignore ) {
		ignored.add( ignore );
		counter.add();
	}

	private void remove( final Predicate<ShouldIgnore> filter ) {
		ignored.removeIf( filter );
		counter.remove();
	}

	private boolean any( final Predicate<ShouldIgnore> ignore ) {
		return ignored.stream().anyMatch( ignore );
	}

	public PersistableGlobalIgnoreApplier persist() {
		return new PersistableGlobalIgnoreApplier( ignored );
	}

	public static class PersistableGlobalIgnoreApplier {

		private final List<ShouldIgnore> ignores;

		public PersistableGlobalIgnoreApplier( final List<ShouldIgnore> ignores ) {
			this.ignores = new ArrayList<>( ignores );
		}

		public List<ShouldIgnore> getIgnores() {
			return ignores;
		}
	}
}
