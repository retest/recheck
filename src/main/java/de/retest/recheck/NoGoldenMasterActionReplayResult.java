package de.retest.recheck;

import java.util.Collections;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.descriptors.SutState;
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
				DifferenceRetriever.of( toStateDifference( actual ) ), 0L );
	}

	private static StateDifference toStateDifference( final SutState actual ) {
		final RootElementDifference rootDiff =
				new RootElementDifferenceFinder( ( comp, attributesKey, value ) -> false ).findDifference( null,
						actual.getRootElements().get( 0 ) );
		return new StateDifference( Collections.singletonList( rootDiff ) );
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
