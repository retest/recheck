package de.retest.recheck;

import java.util.Collections;
import java.util.Set;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.ErrorHolder;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.RootElementDifferenceFinder;
import de.retest.recheck.ui.diff.StateDifference;

public class NoRecheckFileActionReplayResult extends ActionReplayResult {

	private static final String MSG_SHORT = "No recheck file found.";
	static final String MSG_LONG =
			MSG_SHORT + " First time test was run? Created recheck file now, don't forget to commit...";

	private static final long serialVersionUID = 1L;

	public NoRecheckFileActionReplayResult( final String currentStep, final SutState actual ) {
		super( ActionReplayData.withoutTarget( currentStep, null ), WindowRetriever.empty(), ErrorHolder.empty(),
				DifferenceRetriever.of( toStateDifference( actual ) ), 0L, null );
	}

	private static StateDifference toStateDifference( final SutState actual ) {
		final RootElementDifference rootDiff =
				new RootElementDifferenceFinder( ( comp, attributesKey, value ) -> false ).findDifference( null,
						actual.getRootElements().get( 0 ) );
		return new StateDifference( Collections.singletonList( rootDiff ), null );
	}

	@Override
	public Set<Object> getUniqueDifferences() {
		return Collections.singleton( MSG_SHORT );
	}

	@Override
	public String toStringDetailed() {
		return MSG_LONG;
	}

	@Override
	public String toString() {
		return MSG_SHORT;
	}

}
