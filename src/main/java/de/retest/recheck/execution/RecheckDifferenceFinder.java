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
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifferenceFinder;

public class RecheckDifferenceFinder {

	private static final Logger logger = LoggerFactory.getLogger( RecheckDifferenceFinder.class );

	private final RootElementDifferenceFinder finder;
	private final MetadataDifferenceFinder metadataDifferenceFinder = new MetadataDifferenceFinder();

	private final String currentStep;
	private final String goldenMasterPath;

	public RecheckDifferenceFinder( final DefaultValueFinder finder, final String currentStep,
			final String goldenMasterPath ) {
		this.finder = new RootElementDifferenceFinder( finder );
		this.currentStep = currentStep;
		this.goldenMasterPath = goldenMasterPath;
	}

	public ActionReplayResult findDifferences( final SutState actual, final SutState expected ) {
		final List<RootElementDifference> differences =
				finder.findDifferences( expected.getRootElements(), actual.getRootElements() );
		final MetadataDifference metadataDifference = metadataDifferenceFinder.findDifferences( expected, actual );
		if ( !differences.isEmpty() ) {
			return createResult( new StateDifference( differences ), metadataDifference );
		}
		return createEmptyResult( actual, metadataDifference );
	}

	private ActionReplayResult createResult( final StateDifference stateDifference,
			final MetadataDifference metadataDifference ) {
		logger.debug( "Found {} differences for step '{}'.", stateDifference.size(), currentStep );
		return ActionReplayResult.withDifference( ActionReplayData.withoutTarget( currentStep, goldenMasterPath ),
				WindowRetriever.empty(), DifferenceRetriever.of( stateDifference, metadataDifference ), 0L );
	}

	private ActionReplayResult createEmptyResult( final SutState actual, final MetadataDifference metadataDifference ) {
		logger.debug( "Found no differences in step '{}'.", currentStep );
		return ActionReplayResult.withDifference( ActionReplayData.withoutTarget( currentStep, goldenMasterPath ),
				WindowRetriever.of( actual ), DifferenceRetriever.of( metadataDifference ), 0L );
	}

}
