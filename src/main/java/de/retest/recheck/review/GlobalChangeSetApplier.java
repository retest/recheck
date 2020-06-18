package de.retest.recheck.review;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.review.counter.Counter;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.ElementIdentificationWarning;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.AttributeChanges;
import lombok.AccessLevel;
import lombok.Getter;

public class GlobalChangeSetApplier {

	private final Counter counter;

	private GlobalChangeSetApplier( final TestReport testReport, final Counter counter ) {
		this.counter = counter;
		attributeDiffsLookupMap = ArrayListMultimap.create();
		warningsLookup = new HashMap<>();
		insertedDiffsLookupMap = ArrayListMultimap.create();
		deletedDiffsLookupMap = ArrayListMultimap.create();
		actionChangeSetLookupMap = new HashMap<>();

		fillReplayResultLookupMaps( testReport );
	}

	public static GlobalChangeSetApplier create( final TestReport testReport ) {
		return create( testReport, NopCounter.getInstance() );
	}

	public static GlobalChangeSetApplier create( final TestReport testReport, final Counter counter ) {
		return new GlobalChangeSetApplier( testReport, counter );
	}

	// Replay result lookup maps.

	@Getter( AccessLevel.PACKAGE )
	private final Multimap<ImmutablePair<String, String>, ActionReplayResult> attributeDiffsLookupMap;
	@Getter( AccessLevel.PACKAGE )
	private final HashMap<ImmutablePair<String, String>, Set<ElementIdentificationWarning>> warningsLookup;
	@Getter( AccessLevel.PACKAGE )
	private final Multimap<String, ActionReplayResult> insertedDiffsLookupMap;
	@Getter( AccessLevel.PACKAGE )
	private final Multimap<String, ActionReplayResult> deletedDiffsLookupMap;

	private void fillReplayResultLookupMaps( final TestReport testReport ) {
		testReport.getSuiteReplayResults().stream() //
				.flatMap( suiteReplayResult -> suiteReplayResult.getTestReplayResults().stream() ) //
				.flatMap( testReplayResult -> testReplayResult.getActionReplayResults().stream() ) //
				.forEach( this::fillReplayResultLookupMaps );
	}

	private void fillReplayResultLookupMaps( final ActionReplayResult actionReplayResult ) {
		for ( final ElementDifference elementDiff : actionReplayResult.getAllElementDifferences() ) {
			if ( elementDiff.isInsertionOrDeletion() ) {
				fillInsertedDeletedDifferencesLookupMaps( actionReplayResult, elementDiff );
			} else {
				fillAttributeDifferencesLookupMap( actionReplayResult, elementDiff );
			}
		}
	}

	private void fillInsertedDeletedDifferencesLookupMaps( final ActionReplayResult actionReplayResult,
			final ElementDifference elementDiff ) {
		final InsertedDeletedElementDifference insertedDeletedElementDiff =
				(InsertedDeletedElementDifference) elementDiff.getIdentifyingAttributesDifference();
		if ( insertedDeletedElementDiff.isInserted() ) {
			insertedDiffsLookupMap.put( identifier( insertedDeletedElementDiff.getActual() ), actionReplayResult );
		} else {
			deletedDiffsLookupMap.put( identifier( elementDiff.getIdentifyingAttributes() ), actionReplayResult );
		}
	}

	private void fillAttributeDifferencesLookupMap( final ActionReplayResult actionReplayResult,
			final ElementDifference elementDiff ) {
		final IdentifyingAttributes identifyingAttributes = elementDiff.getIdentifyingAttributes();
		for ( final AttributeDifference attributeDifference : elementDiff.getAttributeDifferences() ) {
			final ImmutablePair<String, String> key =
					ImmutablePair.of( identifier( identifyingAttributes ), identifier( attributeDifference ) );
			attributeDiffsLookupMap.put( key, actionReplayResult );
			final List<ElementIdentificationWarning> warnings = attributeDifference.getElementIdentificationWarnings();
			if ( !warnings.isEmpty() ) {
				warningsLookup.computeIfAbsent( key, k -> new HashSet<>() ).addAll( warnings );
			}
		}
	}

	private Collection<ActionReplayResult> findAllActionResultsWithEqualDifferences(
			final IdentifyingAttributes identifyingAttributes, final AttributeDifference attributeDifference ) {
		return attributeDiffsLookupMap
				.get( ImmutablePair.of( identifier( identifyingAttributes ), identifier( attributeDifference ) ) );
	}

	private Set<ElementIdentificationWarning> findAllWarningsForDifference( final IdentifyingAttributes attributes,
			final AttributeDifference difference ) {
		return warningsLookup.getOrDefault( ImmutablePair.of( identifier( attributes ), identifier( difference ) ),
				Collections.emptySet() );
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
			final AttributeChanges changes = correspondingActionChangeSet.getIdentAttributeChanges();
			changes.add( identifyingAttributes, injectWarningsFor( identifyingAttributes, attributeDifference ) );
		}
		counter.add();
	}

	public void createChangeSetForAllEqualAttributesChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			final ActionChangeSet correspondingActionChangeSet = findCorrespondingActionChangeSet( actionReplayResult );
			final AttributeChanges changes = correspondingActionChangeSet.getAttributesChanges();
			changes.add( identifyingAttributes, injectWarningsFor( identifyingAttributes, attributeDifference ) );
		}
		counter.add();
	}

	public void removeChangeSetForAllEqualIdentAttributeChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			final ActionChangeSet correspondingActionChangeSet = findCorrespondingActionChangeSet( actionReplayResult );
			final AttributeChanges changes = correspondingActionChangeSet.getIdentAttributeChanges();
			changes.remove( identifyingAttributes, injectWarningsFor( identifyingAttributes, attributeDifference ) );
		}
		counter.remove();
	}

	public void removeChangeSetForAllEqualAttributesChanges( final IdentifyingAttributes identifyingAttributes,
			final AttributeDifference attributeDifference ) {
		for ( final ActionReplayResult actionReplayResult : findAllActionResultsWithEqualDifferences(
				identifyingAttributes, attributeDifference ) ) {
			final ActionChangeSet correspondingActionChangeSet = findCorrespondingActionChangeSet( actionReplayResult );
			final AttributeChanges changes = correspondingActionChangeSet.getAttributesChanges();
			changes.remove( identifyingAttributes, injectWarningsFor( identifyingAttributes, attributeDifference ) );
		}
		counter.remove();
	}

	private AttributeDifference injectWarningsFor( final IdentifyingAttributes attributes,
			final AttributeDifference difference ) {
		final AttributeDifference copy =
				new AttributeDifference( difference.getKey(), difference.getExpected(), difference.getActual() );
		copy.addElementIdentificationWarnings( findAllWarningsForDifference( attributes, difference ) );
		return copy;
	}

	// Add/remove inserted/deleted differences.

	public void addChangeSetForAllEqualInsertedChanges( final Element inserted ) {
		for ( final ActionReplayResult replayResult : insertedDiffsLookupMap.get( identifier( inserted ) ) ) {
			findCorrespondingActionChangeSet( replayResult ).addInsertChange( inserted );
		}
		counter.add();
	}

	public void addChangeSetForAllEqualDeletedChanges( final IdentifyingAttributes deleted ) {
		for ( final ActionReplayResult replayResult : deletedDiffsLookupMap.get( identifier( deleted ) ) ) {
			findCorrespondingActionChangeSet( replayResult ).addDeletedChange( deleted );
		}
		counter.add();
	}

	public void removeChangeSetForAllEqualInsertedChanges( final Element inserted ) {
		for ( final ActionReplayResult replayResult : insertedDiffsLookupMap.get( identifier( inserted ) ) ) {
			findCorrespondingActionChangeSet( replayResult ).removeInsertChange( inserted );
		}
		counter.remove();
	}

	public void removeChangeSetForAllEqualDeletedChanges( final IdentifyingAttributes deleted ) {
		for ( final ActionReplayResult replayResult : deletedDiffsLookupMap.get( identifier( deleted ) ) ) {
			findCorrespondingActionChangeSet( replayResult ).removeDeletedChange( deleted );
		}
		counter.remove();
	}

	private String identifier( final Element element ) {
		return identifier( element.getIdentifyingAttributes() );
	}

	private String identifier( final IdentifyingAttributes attributes ) {
		return attributes.identifier();
	}

	private String identifier( final AttributeDifference difference ) {
		return difference.identifier();
	}
}
