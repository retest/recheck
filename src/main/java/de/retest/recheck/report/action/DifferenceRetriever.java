package de.retest.recheck.report.action;

import java.util.Collections;

import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;

public interface DifferenceRetriever {

	StateDifference getStateDifference();

	MetadataDifference getMetadataDifference();

	default boolean isNull() {
		return getStateDifference() == null || getStateDifference().size() == 0;
	}

	static DifferenceRetriever empty() {
		return of( new StateDifference( Collections.emptyList() ) );
	}

	static DifferenceRetriever of( final StateDifference stateDifference ) {
		return of( stateDifference, MetadataDifference.empty() );
	}

	static DifferenceRetriever of( final MetadataDifference metadataDifference ) {
		return of( new StateDifference( Collections.emptyList() ), metadataDifference );
	}

	static DifferenceRetriever of( final StateDifference stateDifference,
			final MetadataDifference metadataDifference ) {
		return new DifferenceRetriever() {

			@Override
			public StateDifference getStateDifference() {
				return stateDifference;
			}

			@Override
			public MetadataDifference getMetadataDifference() {
				return metadataDifference;
			}
		};
	}
}
