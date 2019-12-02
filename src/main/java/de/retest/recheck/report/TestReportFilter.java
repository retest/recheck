package de.retest.recheck.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.util.StreamUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestReportFilter {

	private final Filter filter;

	public TestReport filter( final TestReport report ) {
		final TestReport newTestReport = new TestReport();
		for ( final SuiteReplayResult suiteReplayResult : report.getSuiteReplayResults() ) {
			newTestReport.addSuite( filter( suiteReplayResult ) );
		}
		return newTestReport;
	}

	// Filter classes from de.retest.recheck.report.

	public SuiteReplayResult filter( final SuiteReplayResult suiteReplayResult ) {
		final SuiteReplayResult newSuiteReplayResult = new SuiteReplayResult( suiteReplayResult.getName(),
				suiteReplayResult.getSuiteNr(), suiteReplayResult.getExecSuiteSutVersion(),
				suiteReplayResult.getSuiteUuid(), suiteReplayResult.getReplaySutVersion() );
		for ( final TestReplayResult testReplayResult : suiteReplayResult.getTestReplayResults() ) {
			newSuiteReplayResult.addTest( filter( testReplayResult ) );
		}
		return newSuiteReplayResult;
	}

	public TestReplayResult filter( final TestReplayResult testReplayResult ) {
		final TestReplayResult newTestReplayResult =
				new TestReplayResult( testReplayResult.getName(), testReplayResult.getTestNr() );
		for ( final ActionReplayResult actionReplayResult : testReplayResult.getActionReplayResults() ) {
			newTestReplayResult.addAction( filter( actionReplayResult ) );
		}
		return newTestReplayResult;
	}

	public ActionReplayResult filter( final ActionReplayResult actionReplayResult ) {
		if ( actionReplayResult instanceof NoGoldenMasterActionReplayResult ) {
			return actionReplayResult;
		}

		final ActionReplayData data = ActionReplayData.withTarget( actionReplayResult.getDescription(),
				actionReplayResult.getTargetComponent(), actionReplayResult.getGoldenMasterPath() );
		final StateDifference newStateDiff = filter( actionReplayResult.getStateDifference() );
		final long actualDuration = actionReplayResult.getDuration();

		return ActionReplayResult.withDifference( data, actionReplayResult::getWindows,
				DifferenceRetriever.of( newStateDiff ), actualDuration );
	}

	// Filter classes from de.retest.recheck.ui.diff.

	StateDifference filter( final StateDifference stateDiff ) {
		if ( stateDiff == null || stateDiff.getRootElementDifferences().isEmpty() ) {
			return stateDiff;
		}
		final List<RootElementDifference> newRootElementDiffs = filter( stateDiff.getRootElementDifferences() );
		return new StateDifference( newRootElementDiffs );
	}

	List<RootElementDifference> filter( final List<RootElementDifference> rootElementDiffs ) {
		return rootElementDiffs.stream() //
				.map( this::filter ) //
				.flatMap( StreamUtil::optionalToStream ) //
				.collect( toList() );
	}

	Optional<RootElementDifference> filter( final RootElementDifference rootElementDiff ) {
		return filter( rootElementDiff.getElementDifference() ) //
				.map( newElementDifference -> new RootElementDifference( newElementDifference,
						rootElementDiff.getExpectedDescriptor(), rootElementDiff.getActualDescriptor() ) );
	}

	Optional<ElementDifference> filter( final ElementDifference elementDiff ) {
		final Element element = elementDiff.getElement();

		AttributesDifference attributesDiff = elementDiff.getAttributesDifference();
		LeafDifference identAttributesDiff = elementDiff.getIdentifyingAttributesDifference();
		Collection<ElementDifference> childDiffs = elementDiff.getChildDifferences();

		if ( elementDiff.hasAttributesDifferences() ) {
			attributesDiff = filter( element, attributesDiff ).orElse( null );
		}

		if ( elementDiff.hasIdentAttributesDifferences() ) {
			identAttributesDiff =
					filter( element, (IdentifyingAttributesDifference) identAttributesDiff ).orElse( null );
		} else if ( elementDiff.isInsertionOrDeletion() ) {
			identAttributesDiff = filter( (InsertedDeletedElementDifference) identAttributesDiff ).orElse( null );
		}

		if ( elementDiff.hasChildDifferences() ) {
			childDiffs = filter( childDiffs );
		}

		final ElementDifference newElementDiff = new ElementDifference( element, attributesDiff, identAttributesDiff,
				elementDiff.getExpectedScreenshot(), elementDiff.getActualScreenshot(), childDiffs );
		final boolean anyOwnOrChildDiffs = newElementDiff.hasAnyDifference() || newElementDiff.hasChildDifferences();
		return anyOwnOrChildDiffs ? Optional.of( newElementDiff ) : Optional.empty();
	}

	Optional<AttributesDifference> filter( final Element element, final AttributesDifference attributesDiff ) {
		return attributesDiff.getDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( collectingAndThen( toList(), newDiffs -> newDiffs.isEmpty() //
						? Optional.empty() //
						: Optional.of( new AttributesDifference( newDiffs ) ) ) );
	}

	Optional<IdentifyingAttributesDifference> filter( final Element element,
			final IdentifyingAttributesDifference identAttributesDiff ) {
		return identAttributesDiff.getAttributeDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( collectingAndThen( toList(), newDiffs -> newDiffs.isEmpty() //
						? Optional.empty() //
						: Optional.of( new IdentifyingAttributesDifference( element.getIdentifyingAttributes(),
								newDiffs ) ) ) );
	}

	Optional<InsertedDeletedElementDifference> filter( final InsertedDeletedElementDifference insertedDeletedDiff ) {
		return filter.matches( insertedDeletedDiff.getInsertedOrDeletedElement() ) //
				? Optional.empty() //
				: Optional.of( insertedDeletedDiff );
	}

	Collection<ElementDifference> filter( final Collection<ElementDifference> elementDiffs ) {
		return elementDiffs.stream() //
				.map( this::filter ) //
				.flatMap( StreamUtil::optionalToStream ) //
				.collect( toList() );
	}
}
