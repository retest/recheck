package de.retest.recheck.report;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.ui.actions.ExceptionWrapper;
import de.retest.recheck.ui.actions.TargetNotFoundException;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
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

	SuiteReplayResult filter( final SuiteReplayResult suiteReplayResult ) {
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

	ActionReplayResult filter( final ActionReplayResult actionReplayResult ) {
		if ( actionReplayResult instanceof NoGoldenMasterActionReplayResult ) {
			return actionReplayResult;
		}

		final ActionReplayData data = ActionReplayData.withTarget( actionReplayResult.getDescription(),
				actionReplayResult.getTargetComponent(), actionReplayResult.getGoldenMasterPath() );
		final ExceptionWrapper error = actionReplayResult.getThrowableWrapper();
		final TargetNotFoundException tnfe = (TargetNotFoundException) actionReplayResult.getTargetNotFoundException();
		final StateDifference newStateDiff = filter( actionReplayResult.getStateDifference() );
		final long actualDuration = actionReplayResult.getDuration();
		final SutState actualState = new SutState( actionReplayResult.getWindows() );

		return ActionReplayResult.createActionReplayResult( data, error, tnfe, newStateDiff, actualDuration,
				actualState );
	}

	StateDifference filter( final StateDifference stateDiff ) {
		if ( stateDiff == null || stateDiff.getRootElementDifferences().isEmpty() ) {
			return stateDiff;
		}
		final List<RootElementDifference> newRootElementDiffs = filter( stateDiff.getRootElementDifferences() );
		return new StateDifference( newRootElementDiffs, stateDiff.getDurationDifference() );
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
		AttributesDifference attributesDiff = elementDiff.getAttributesDifference();
		LeafDifference identAttributesDiff = elementDiff.getIdentifyingAttributesDifference();
		Collection<ElementDifference> childDiffs = elementDiff.getChildDifferences();
		if ( elementDiff.hasAttributesDifferences() ) {
			attributesDiff = filter( elementDiff.getElement(), elementDiff.getAttributesDifference() ).orElse( null );
		}
		if ( elementDiff.hasIdentAttributesDifferences() ) {
			identAttributesDiff = filter( elementDiff.getElement(),
					(IdentifyingAttributesDifference) elementDiff.getIdentifyingAttributesDifference() ).orElse( null );
		}
		if ( !elementDiff.getChildDifferences().isEmpty() ) {
			childDiffs = filter( elementDiff.getChildDifferences() );
		}
		final ElementDifference newElementDiff =
				new ElementDifference( elementDiff.getElement(), attributesDiff, identAttributesDiff,
						elementDiff.getExpectedScreenshot(), elementDiff.getActualScreenshot(), childDiffs );
		return newElementDiff.hasAnyDifference() ? Optional.of( newElementDiff ) : Optional.empty();
	}

	Collection<ElementDifference> filter( final Collection<ElementDifference> elementDiffs ) {
		return elementDiffs.stream() //
				.map( this::filter ) //
				.flatMap( StreamUtil::optionalToStream ) //
				.collect( toList() );
	}

	Optional<IdentifyingAttributesDifference> filter( final Element element,
			final IdentifyingAttributesDifference identAttributesDiff ) {
		return identAttributesDiff.getAttributeDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( collectingAndThen( toList(), newDiffs -> newDiffs.isEmpty() //
						? Optional.empty() //
						: Optional.of( new IdentifyingAttributesDifference( element.getIdentifyingAttributes(), newDiffs ) ) ) );
	}

	Optional<AttributesDifference> filter( final Element element, final AttributesDifference attributesDiff ) {
		return attributesDiff.getDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( collectingAndThen( toList(), newDiffs -> newDiffs.isEmpty() //
						? Optional.empty() //
						: Optional.of( new AttributesDifference( newDiffs ) ) ) );
	}
}
