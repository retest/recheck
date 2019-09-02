package de.retest.recheck;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.ErrorHolder;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.RootElementDifferenceFinder;
import de.retest.recheck.ui.diff.StateDifference;

public class NoGoldenMasterActionReplayResult extends ActionReplayResult {

	private static final String MSG_SHORT = "No Golden Master found.";
	public static final String MSG_LONG =
			MSG_SHORT + " First time test was run? Created new Golden Master, so don't forget to commit...";

	private static final long serialVersionUID = 1L;

	public NoGoldenMasterActionReplayResult( final String currentStep, final SutState actual,
			final String goldenMasterPath ) {
		super( ActionReplayData.withoutTarget( currentStep, goldenMasterPath ), WindowRetriever.empty(),
				ErrorHolder.empty(), DifferenceRetriever.of( toStateDifference( actual ) ), 0L, null );
	}

	private static StateDifference toStateDifference( final SutState actual ) {
		final RootElementDifference rootDiff =
				new RootElementDifferenceFinder( ( comp, attributesKey, value ) -> false ).findDifference( null,
						actual.getRootElements().get( 0 ) );
		return new StateDifference( Collections.singletonList( rootDiff ), null );
	}

	@Override
	public Set<LeafDifference> getDifferencesWithout( final Filter filter ) {
		return Collections.singleton( new LeafDifference() {

			@Override
			public int size() {
				return 0;
			}

			@Override
			public List<ElementDifference> getNonEmptyDifferences() {
				return Collections.emptyList();
			}

			@Override
			public List<ElementDifference> getElementDifferences() {
				return Collections.emptyList();
			}

			@Override
			public Serializable getActual() {
				return null;
			}

			@Override
			public Serializable getExpected() {
				return null;
			}

			@Override
			public String toString() {
				return MSG_SHORT;
			}
		} );
	}

	@Override
	public String toString() {
		return MSG_SHORT;
	}

	@Override
	public boolean hasDifferences() {
		return true;
	}
}
