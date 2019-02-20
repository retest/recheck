package de.retest.recheck.review;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.counter.IntCounter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.review.ActionChangeSet;

public class GlobalChangeSetApplier {

	private static final ShouldIgnore SHOULD_IGNORE_NOTHING = null;

	private final Counter counter;

	private GlobalChangeSetApplier( final ReplayResult replayResult, final Counter counter ) {
		this.counter = counter;
		attributeDiffsLookupMap = ArrayListMultimap.create();
		insertedDiffsLookupMap = ArrayListMultimap.create();
		deletedDiffsLookupMap = ArrayListMultimap.create();
		actionChangeSetLookupMap = new HashMap<>();

		fillReplayResultLookupMaps( replayResult );
	}

	public static GlobalChangeSetApplier create( final ReplayResult replayResult ) {
		return create( replayResult, HasUnsafedChangesListener.NOOP_LISTENER );
	}

	public static GlobalChangeSetApplier create( final ReplayResult replayResult,
			final HasUnsafedChangesListener listener ) {
		return new GlobalChangeSetApplier( replayResult, new IntCounter( listener ) );
	}

	// Replay result lookup maps.

	private final Multimap<ImmutablePair<IdentifyingAttributes, AttributeDifference>, ActionReplayResult> attributeDiffsLookupMap;
	private final Multimap<Element, ActionReplayResult> insertedDiffsLookupMap;
	private final Multimap<IdentifyingAttributes, ActionReplayResult> deletedDiffsLookupMap;

	private void fillReplayResultLookupMaps( final ReplayResult replayResult ) {
		for ( final SuiteReplayResult suiteReplayResult : replayResult.getSuiteReplayResults() ) {
			for ( final TestReplayResult testReplayResult : suiteReplayResult.getTestReplayResults() ) {
				for ( final ActionReplayResult actionReplayResult : testReplayResult.getActionReplayResults() ) {
					for ( final ElementDifference componentDiff : actionReplayResult.getAllElementDifferences() ) {
						if ( componentDiff.isInsertionOrDeletion() ) {
							fillInsertedDeletedDifferencesLookupMaps( actionReplayResult, componentDiff );
						} else {
							fillAttributeDifferencesLookupMap( actionReplayResult, componentDiff );
						}
					}
				}
			}
		}
	}

	private void fillInsertedDeletedDifferencesLookupMaps( final ActionReplayResult actionReplayResult,
			final ElementDifference componentDiff ) {
		final InsertedDeletedElementDifference insertedDeletedComponentDiff =
				(InsertedDeletedElementDifference) componentDiff.getIdentifyingAttributesDifference();
		if ( insertedDeletedComponentDiff.isInserted() ) {
			insertedDiffsLookupMap.put( insertedDeletedComponentDiff.getActual(), actionReplayResult );
		} else {
			deletedDiffsLookupMap.put( componentDiff.getIdentifyingAttributes(), actionReplayResult );
		}
	}

	private void fillAttributeDifferencesLookupMap( final ActionReplayResult actionReplayResult,
			final ElementDifference componentDiff ) {
		final IdentifyingAttributes identifyingAttributes = componentDiff.getIdentifyingAttributes();
		for ( final AttributeDifference attributeDifference : componentDiff
				.getAttributeDifferences( SHOULD_IGNORE_NOTHING ) ) {
			attributeDiffsLookupMap.put( ImmutablePair.of( identifyingAttributes, attributeDifference ),
					actionReplayResult );
		}
	}

	private Collection<ActionReplayResult> findAllActionResultsWithEqualDifferences(
			final IdentifyingAttributes identifyingAttributes, final AttributeDifference attributeDifference ) {
		return attributeDiffsLookupMap.get( ImmutablePair.of( identifyingAttributes, attributeDifference ) );
	}

	private ActionChangeSet findCorrespondingActionChangeSet( final ActionReplayResult actionReplayResult ) {
		final ActionChangeSet actionChangeSet = actionChangeSetLookupMap.get( actionReplayResult );
		assert actionChangeSet != null : "Error, introduce() wasn't called for this actionReplayResult!";
		return actionChangeSet;
	}

	// Action change set lookup map.

	private final Map<ActionReplayResult, ActionChangeSet> actionChangeSetLookupMap;

	public void introduce( final ActionReplayResult actionReplayResult, final ActionChangeSet actionChangeSet ) {
		actionChangeSetLookupMap.put( actionReplayResult, actionChangeSet );
	}

	// Add/remove element differences.

	public void addChangeSetForAllEqualIdentAttributeChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		final Collection<ActionReplayResult> actionResultsWithDiffs =
				findAllActionResultsWithEqualDifferences( identifyingAttributes, attributeDifference );
		assert !actionResultsWithDiffs.isEmpty() : "Should have been added during load and thus not be empty!";
		for ( final ActionReplayResult actionReplayResult : actionResultsWithDiffs ) {
			final ActionChangeSet correspondingActionChangeSet = findCorrespondingActionChangeSet( actionReplayResult );
			assert correspondingActionChangeSet != null : "Should have been added during load and thus not be empty!";
			correspondingActionChangeSet.getIdentAttributeChanges().add( identifyingAttributes, attributeDifference );
			counter.add();
		}
	}

	public void createChangeSetForAllEqualAttributesChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			findCorrespondingActionChangeSet( actionReplayResult ).getAttributesChanges().add( identifyingAttributes,
					attributeDifference );
			counter.add();
		}
	}

	public void removeChangeSetForAllEqualIdentAttributeChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			findCorrespondingActionChangeSet( actionReplayResult ).getIdentAttributeChanges()
					.remove( identifyingAttributes, attributeDifference );
			counter.remove();
		}
	}

	public void removeChangeSetForAllEqualAttributesChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			findCorrespondingActionChangeSet( actionReplayResult ).getAttributesChanges().remove( identifyingAttributes,
					attributeDifference );
			counter.remove();
		}
	}

	// Add/remove inserted/deleted differences.

	public void addChangeSetForAllEqualInsertedChanges( final Element inserted ) {
		for ( final ActionReplayResult replayResult : insertedDiffsLookupMap.get( inserted ) ) {
			findCorrespondingActionChangeSet( replayResult ).addInsertChange( inserted );
			counter.add();
		}
	}

	public void addChangeSetForAllEqualDeletedChanges( final IdentifyingAttributes deleted ) {
		for ( final ActionReplayResult replayResult : deletedDiffsLookupMap.get( deleted ) ) {
			findCorrespondingActionChangeSet( replayResult ).addDeletedChange( deleted );
			counter.add();
		}
	}

	public void removeChangeSetForAllEqualInsertedChanges( final Element inserted ) {
		for ( final ActionReplayResult replayResult : insertedDiffsLookupMap.get( inserted ) ) {
			findCorrespondingActionChangeSet( replayResult ).removeInsertChange( inserted );
			counter.remove();
		}
	}

	public void removeChangeSetForAllEqualDeletedChanges( final IdentifyingAttributes deleted ) {
		for ( final ActionReplayResult replayResult : deletedDiffsLookupMap.get( deleted ) ) {
			findCorrespondingActionChangeSet( replayResult ).removeDeletedChange( deleted );
			counter.remove();
		}
	}

	public int getNumberOfChanges() {
		return counter.getCount();
	}
}
