package de.retest.recheck.execution;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.RootElementDifferenceFinder;
import de.retest.recheck.ui.diff.StateDifference;

public class RecheckDifferenceFinder {

	private static final Logger logger = LoggerFactory.getLogger( RecheckDifferenceFinder.class );

	private final RootElementDifferenceFinder finder;
	private final String currentStep;
	private final String goldenMasterPath;

	public RecheckDifferenceFinder( final DefaultValueFinder finder, final String currentStep,
			final String goldenMasterPath ) {
		this.finder = new RootElementDifferenceFinder( finder );
		this.currentStep = currentStep;
		this.goldenMasterPath = goldenMasterPath;
	}

	public ActionReplayResult findDifferences( final SutState actual, final SutState expected ) {
		return toActionReplayResult( actual, findDifferencesBetweenStates( actual, expected ) );
	}

	private List<RootElementDifference> findDifferencesBetweenStates( final SutState actual, final SutState expected ) {
		return finder.findDifferences( expected.getRootElements(), actual.getRootElements() );
	}

	private ActionReplayResult toActionReplayResult( final SutState actual,
			final List<RootElementDifference> differences ) {
		if ( differences != null && !differences.isEmpty() ) {
			logger.debug( "Found {} differences for step '{}'.", differences.size(), currentStep );
			return ActionReplayResult.withDifference( ActionReplayData.withoutTarget( currentStep, goldenMasterPath ),
					WindowRetriever.empty(), DifferenceRetriever.of( new StateDifference( differences ) ), 0L );
		}
		logger.debug( "Found no differences in step '{}'.", currentStep );
		return ActionReplayResult.withoutDifference( ActionReplayData.withoutTarget( currentStep, goldenMasterPath ),
				WindowRetriever.of( actual ), 0L );
	}
}
