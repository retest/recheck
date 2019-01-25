package de.retest.recheck.execution;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.report.ActionReplayResult;
import de.retest.report.action.ActionReplayData;
import de.retest.report.action.DifferenceRetriever;
import de.retest.report.action.WindowRetriever;
import de.retest.ui.DefaultValueFinder;
import de.retest.ui.descriptors.SutState;
import de.retest.ui.diff.DifferenceResult;
import de.retest.ui.diff.RootElementDifference;
import de.retest.ui.diff.RootElementDifferenceFinder;

public class RecheckDifferenceFinder {

	private static final Logger logger = LoggerFactory.getLogger( RecheckDifferenceFinder.class );

	private final RootElementDifferenceFinder finder;
	private final String currentStep;
	private final String stateFilePath;

	public RecheckDifferenceFinder( final DefaultValueFinder finder, final String currentStep,
			final String stateFilePath ) {
		this.finder = new RootElementDifferenceFinder( finder );
		this.currentStep = currentStep;
		this.stateFilePath = stateFilePath;
	}

	public ActionReplayResult findDifferences( final SutState actual, final SutState expected ) {
		return toActionReplayResult( new DifferenceResult( actual, findDifferencesBetweenStates( actual, expected ) ) );
	}

	private List<RootElementDifference> findDifferencesBetweenStates( final SutState actual, final SutState expected ) {
		return finder.findDifferences( expected.getRootElements(), actual.getRootElements() );
	}

	private ActionReplayResult toActionReplayResult( final DifferenceResult check ) {
		final List<RootElementDifference> differences = check.getDifferences();
		if ( differences != null && differences.size() > 0 ) {
			logger.info( "Found {} differences for step '{}'.", differences.size(), currentStep );
			return ActionReplayResult.withDifference( ActionReplayData.withoutTarget( currentStep, stateFilePath ),
					WindowRetriever.empty(), DifferenceRetriever.of( differences ), 0L );
		}
		logger.info( "Found no differences in step '{}'.", currentStep );
		return ActionReplayResult.withoutDifference( ActionReplayData.withoutTarget( currentStep, stateFilePath ),
				WindowRetriever.of( check.getCurrentSutState() ), 0L );
	}
}
