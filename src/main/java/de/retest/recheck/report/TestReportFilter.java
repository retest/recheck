package de.retest.recheck.report;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		final TargetNotFoundException targetNotFound =
				(TargetNotFoundException) actionReplayResult.getTargetNotFoundException();
		final StateDifference newStateDifference = filter( actionReplayResult.getStateDifference() );
		final long actualDuration = actionReplayResult.getDuration();
		final SutState actualState = new SutState( actionReplayResult.getWindows() );
		return ActionReplayResult.createActionReplayResult( data, error, targetNotFound, newStateDifference,
				actualDuration, actualState );
	}

	StateDifference filter( final StateDifference stateDifference ) {
		if ( stateDifference == null || stateDifference.getRootElementDifferences().isEmpty() ) {
			return stateDifference;
		}
		final List<RootElementDifference> newRootElementDifferences =
				filter( stateDifference.getRootElementDifferences() );
		return new StateDifference( newRootElementDifferences, stateDifference.getDurationDifference() );
	}

	List<RootElementDifference> filter( final List<RootElementDifference> rootElementDifferences ) {
		return rootElementDifferences.stream() //
				.map( rootElementDifference -> filter( rootElementDifference ) ) //
				.flatMap( newRootElementDifference -> newRootElementDifference.map( Stream::of ) //
						.orElseGet( Stream::empty ) ) //
				.collect( Collectors.toList() );
	}

	Optional<RootElementDifference> filter( final RootElementDifference rootElementDifference ) {
		return filter( rootElementDifference.getElementDifference() ) //
				.map( newRootElementDifference -> new RootElementDifference( newRootElementDifference,
						rootElementDifference.getExpectedDescriptor(), rootElementDifference.getActualDescriptor() ) );
	}

	Optional<ElementDifference> filter( final ElementDifference elementDiff ) {
		AttributesDifference attributesDifference = elementDiff.getAttributesDifference();
		LeafDifference identifyingAttributesDifference = elementDiff.getIdentifyingAttributesDifference();
		Collection<ElementDifference> childDifferences = elementDiff.getChildDifferences();
		if ( elementDiff.hasAttributesDifferences() ) {
			attributesDifference = filter( elementDiff.getElement(), elementDiff.getAttributesDifference() );
		}
		if ( elementDiff.hasIdentAttributesDifferences() ) {
			identifyingAttributesDifference = filter( elementDiff.getElement(),
					(IdentifyingAttributesDifference) elementDiff.getIdentifyingAttributesDifference() );
		}
		if ( !elementDiff.getChildDifferences().isEmpty() ) {
			childDifferences = filter( elementDiff.getChildDifferences() );
		}
		final ElementDifference newElementDiff =
				new ElementDifference( elementDiff.getElement(), attributesDifference, identifyingAttributesDifference,
						elementDiff.getExpectedScreenshot(), elementDiff.getActualScreenshot(), childDifferences );
		return newElementDiff.hasAnyDifference() ? Optional.of( newElementDiff ) : Optional.empty();
	}

	Collection<ElementDifference> filter( final Collection<ElementDifference> elementDifferences ) {
		return elementDifferences.stream() //
				.map( elementDifference -> filter( elementDifference ) ) //
				.flatMap( newElementDifference -> newElementDifference.map( Stream::of ) //
						.orElseGet( Stream::empty ) ) //
				.collect( Collectors.toList() );
	}

	IdentifyingAttributesDifference filter( final Element element,
			final IdentifyingAttributesDifference identAttributesDiff ) {
		return identAttributesDiff.getAttributeDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), diffs -> diffs.isEmpty() //
						? null // expected by ElementDifference
						: new IdentifyingAttributesDifference( element.getIdentifyingAttributes(), diffs ) ) );
	}

	AttributesDifference filter( final Element element, final AttributesDifference attributesDiff ) {
		return attributesDiff.getDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), diffs -> diffs.isEmpty() //
						? null // expected by ElementDifference
						: new AttributesDifference( diffs ) ) );
	}
}
