package de.retest.recheck.report.action;

import java.util.List;
import java.util.function.Supplier;

import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

@FunctionalInterface
public interface DifferenceRetriever extends Supplier<StateDifference> {

	default boolean isNull() {
		return get() == null;
	}

	static DifferenceRetriever empty() {
		return () -> null;
	}

	static DifferenceRetriever of( final List<RootElementDifference> differences ) {
		return of( new StateDifference( differences, null ) );
	}

	static DifferenceRetriever of( final StateDifference difference ) {
		return () -> difference;
	}
}
