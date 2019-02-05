package de.retest.recheck.report.action;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import de.retest.recheck.ui.actions.ActionState;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;

@FunctionalInterface
public interface WindowRetriever extends Supplier<List<RootElement>> {

	default boolean isNull() {
		return get() == null;
	}

	static WindowRetriever empty() {
		return Collections::emptyList;
	}

	static WindowRetriever of( final List<RootElement> elements ) {
		return () -> elements;
	}

	static WindowRetriever of( final SutState state ) {
		return state::getRootElements;
	}

	static WindowRetriever of( final ActionState state ) {
		return of( state.getState() );
	}
}
