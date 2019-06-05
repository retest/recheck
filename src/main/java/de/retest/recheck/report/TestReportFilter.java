package de.retest.recheck.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

public class TestReportFilter {

	private TestReportFilter() {
	}

	public static TestReport filter( final TestReport report, final Filter filter ) {
		final TestReport newTestReport = new TestReport();
		for ( final SuiteReplayResult suiteReplayResult : report.getSuiteReplayResults() ) {
			newTestReport.addSuite( filter( suiteReplayResult, filter ) );
		}
		return newTestReport;
	}

	static SuiteReplayResult filter( final SuiteReplayResult suiteReplayResult, final Filter filter ) {
		final SuiteReplayResult newSuiteReplayResult = new SuiteReplayResult( suiteReplayResult.getSuite(),
				suiteReplayResult.getSuiteNr(), suiteReplayResult.getGroundState() );
		for ( final TestReplayResult testReplayResult : suiteReplayResult.getTestReplayResults() ) {
			newSuiteReplayResult.addTest( filter( testReplayResult, filter ) );
		}
		return newSuiteReplayResult;
	}

	static TestReplayResult filter( final TestReplayResult testReplayResult, final Filter filter ) {
		final TestReplayResult newTestReplayResult =
				new TestReplayResult( testReplayResult.getName(), testReplayResult.getTestNr() );
		for ( final ActionReplayResult actionReplayResult : testReplayResult.getActionReplayResults() ) {
			newTestReplayResult.addAction( filter( actionReplayResult, filter ) );
		}
		return newTestReplayResult;
	}

	static ActionReplayResult filter( final ActionReplayResult actionReplayResult, final Filter filter ) {
		final ActionReplayData data = ActionReplayData.withTarget( actionReplayResult.getDescription(),
				actionReplayResult.getTargetComponent(), actionReplayResult.getGoldenMasterPath() );
		final ExceptionWrapper error = actionReplayResult.getThrowableWrapper();
		final TargetNotFoundException targetNotFound =
				(TargetNotFoundException) actionReplayResult.getTargetNotFoundException();
		final StateDifference newStateDifference = filter( actionReplayResult.getStateDifference(), filter );
		final long actualDuration = actionReplayResult.getDuration();
		final SutState actualState = new SutState( actionReplayResult.getWindows() );
		final ActionReplayResult newActionReplayResult = ActionReplayResult.createActionReplayResult( data, error,
				targetNotFound, newStateDifference, actualDuration, actualState );
		return newActionReplayResult;
	}

	static StateDifference filter( final StateDifference stateDifference, final Filter filter ) {
		final List<RootElementDifference> newRootElementDifferences =
				filter( stateDifference.getRootElementDifferences(), filter );
		final StateDifference newStateDifference =
				new StateDifference( newRootElementDifferences, stateDifference.getDurationDifference() );
		return newStateDifference;
	}

	static List<RootElementDifference> filter( final List<RootElementDifference> rootElementDifferences,
			final Filter filter ) {
		final List<RootElementDifference> newRootElementDifferences = new ArrayList<>();
		for ( final RootElementDifference rootElementDifference : rootElementDifferences ) {
			newRootElementDifferences.add( filter( rootElementDifference, filter ) );
		}
		return newRootElementDifferences;
	}

	static RootElementDifference filter( final RootElementDifference rootElementDifference, final Filter filter ) {
		final ElementDifference newElementDifference = filter( rootElementDifference.getElementDifference(), filter );
		final RootElementDifference newRootElementDifference = new RootElementDifference( newElementDifference,
				rootElementDifference.getExpectedDescriptor(), rootElementDifference.getActualDescriptor() );
		return newRootElementDifference;
	}

	static ElementDifference filter( final ElementDifference elementDiff, final Filter filter ) {
		AttributesDifference attributesDifference = elementDiff.getAttributesDifference();
		LeafDifference identifyingAttributesDifference = elementDiff.getIdentifyingAttributesDifference();
		Collection<ElementDifference> childDifferences = elementDiff.getChildDifferences();
		if ( elementDiff.hasAttributesDifferences() ) {
			attributesDifference = filter( elementDiff.getElement(), elementDiff.getAttributesDifference(), filter );
		}
		if ( elementDiff.hasIdentAttributesDifferences() ) {
			identifyingAttributesDifference = filter( elementDiff.getElement(),
					(IdentifyingAttributesDifference) elementDiff.getIdentifyingAttributesDifference(), filter );
		}
		if ( !elementDiff.getChildDifferences().isEmpty() ) {
			childDifferences = filter( elementDiff.getChildDifferences(), filter );
		}
		final ElementDifference newElementDiff =
				new ElementDifference( elementDiff.getElement(), attributesDifference, identifyingAttributesDifference,
						elementDiff.getExpectedScreenshot(), elementDiff.getActualScreenshot(), childDifferences );
		return newElementDiff;
	}

	static Collection<ElementDifference> filter( final Collection<ElementDifference> elementDifferences,
			final Filter filter ) {
		final List<ElementDifference> newElementDifferences = new ArrayList<>();
		for ( final ElementDifference elementDifference : elementDifferences ) {
			newElementDifferences.add( filter( elementDifference, filter ) );
		}
		return newElementDifferences;
	}

	static IdentifyingAttributesDifference filter( final Element element,
			final IdentifyingAttributesDifference identAttributesDiff, final Filter filter ) {
		return identAttributesDiff.getAttributeDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(),
						diffs -> new IdentifyingAttributesDifference( element.getIdentifyingAttributes(), diffs ) ) );
	}

	static AttributesDifference filter( final Element element, final AttributesDifference attributesDiff,
			final Filter filter ) {
		return attributesDiff.getDifferences().stream() //
				.filter( diff -> !filter.matches( element, diff ) ) //
				.collect( Collectors.collectingAndThen( Collectors.toList(), AttributesDifference::new ) );
	}
}
