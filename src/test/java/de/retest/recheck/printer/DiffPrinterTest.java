package de.retest.recheck.printer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.retest.recheck.elementcollection.ElementCollection;
import de.retest.recheck.elementcollection.RecheckIgnore;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.actions.Action;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.GroundState;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

public class DiffPrinterTest {

	@BeforeClass
	public static void setup() {
		RecheckIgnore.getTestInstance( new ElementCollection() );
	}

	@Test
	public void null_should_not_cause_exc() {
		final DiffPrinter cut = new DiffPrinter( null, System.out );

		final ReplayResult report =
				mockReplayResult( singletonList( "Call-to-Action" ), singletonList( "elementDiff" ) );

		final String result = cut.generateFilteredDiffString( report );
		assertThat( result ).isEqualTo( "Check 'Call-to-Action' resulted in:\n\telementDiff\n" );
	}

	@Test
	public void should_print_all_checks_if_none_given() {
		final DiffPrinter cut = new DiffPrinter( null, System.out );

		final ReplayResult report = mockReplayResult( asList( "Call-to-Action-1", "Call-to-Action-2" ),
				asList( "elementDiff-1", "elementDiff-2", "elementDiff-3" ) );

		final String result = cut.generateFilteredDiffString( report );
		assertThat( result ).isEqualTo( "Check 'Call-to-Action-1' resulted in:" //
				+ "\n\telementDiff-1" //
				+ "\n\telementDiff-2" //
				+ "\n\telementDiff-3\n" //
				+ "Check 'Call-to-Action-2' resulted in:" //
				+ "\n\telementDiff-1" //
				+ "\n\telementDiff-2" //
				+ "\n\telementDiff-3\n" );
	}

	@Test
	public void should_print_only_given_checks() {
		final DiffPrinter cut = new DiffPrinter( singletonList( "Call-to-Action-1" ), System.out );

		final ReplayResult report = mockReplayResult( asList( "Call-to-Action-1", "Call-to-Action-2" ),
				asList( "elementDiff-1", "elementDiff-2", "elementDiff-3" ) );

		final String result = cut.generateFilteredDiffString( report );
		assertThat( result ).isEqualTo( "Check 'Call-to-Action-1' resulted in:" //
				+ "\n\telementDiff-1" //
				+ "\n\telementDiff-2" //
				+ "\n\telementDiff-3\n" );
	}

	private ReplayResult mockReplayResult( final List<String> checks, final List<String> diffs ) {
		final List<ElementDifference> elementDiffs = new ArrayList<>();

		for ( final String diff : diffs ) {
			final ElementDifference elementDiff = mock( ElementDifference.class );
			when( elementDiff.toString() ).thenReturn( diff );
			elementDiffs.add( elementDiff );
		}

		final RootElementDifference rootDiff = mock( RootElementDifference.class );
		when( rootDiff.getNonEmptyDifferences() ).thenReturn( elementDiffs );

		final List<RootElementDifference> differences = singletonList( rootDiff );
		final TestReplayResult testResult = new TestReplayResult( "testName", 1 );

		for ( final String checkName : checks ) {
			final Action action = mock( Action.class );
			when( action.toString() ).thenReturn( checkName );
			final StateDifference difference = new StateDifference( differences, null );
			final ActionReplayResult actionResult = ActionReplayResult.withDifference( ActionReplayData.of( action ),
					WindowRetriever.empty(), DifferenceRetriever.of( difference ), 12L );
			testResult.addAction( actionResult );
		}

		final SuiteReplayResult suiteResult =
				new SuiteReplayResult( mock( ExecutableSuite.class ), 1, mock( GroundState.class ) );
		suiteResult.addTest( testResult );
		return new ReplayResult( suiteResult );
	}

	@Test
	public void should_print_every_deeper_element_further_indented() {
		final List<AttributeDifference> attributes =
				Arrays.asList( new AttributeDifference( "attribute", "rock", "rick" ),
						new AttributeDifference( "attribute2", "13", "4711" ) );

		final AttributesDifference attributeDiffs = mock( AttributesDifference.class );
		when( attributeDiffs.getDifferences() ).thenReturn( attributes );

		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "Type [text]" );

		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "id" );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final ElementDifference elementDiff =
				new ElementDifference( element, attributeDiffs, null, null, null, Collections.emptyList() );

		final RootElementDifference rootDiff = mock( RootElementDifference.class );
		when( rootDiff.getNonEmptyDifferences() ).thenReturn( Collections.singletonList( elementDiff ) );

		final TestReplayResult testResult = new TestReplayResult( "testName", 1 );

		final Action action = mock( Action.class );
		when( action.toString() ).thenReturn( "testCheck" );

		final StateDifference difference = new StateDifference( Collections.singletonList( rootDiff ), null );
		final ActionReplayResult actionResult = ActionReplayResult.withDifference( ActionReplayData.of( action ),
				WindowRetriever.empty(), DifferenceRetriever.of( difference ), 12L );
		testResult.addAction( actionResult );

		final SuiteReplayResult suiteResult =
				new SuiteReplayResult( mock( ExecutableSuite.class ), 1, mock( GroundState.class ) );
		suiteResult.addTest( testResult );

		final ReplayResult report = new ReplayResult( suiteResult );

		final DiffPrinter cut = new DiffPrinter( System.out );

		final String result = cut.generateFilteredDiffString( report );
		assertThat( result ).isEqualTo( "Check 'testCheck' resulted in:" //
				+ "\n\tType [text]:" //
				+ "\n\t at: null:" //
				+ "\n\t\t" + "attribute: expected=\"rock\", actual=\"rick\"" //
				+ "\n\t\t" + "attribute2: expected=\"13\", actual=\"4711\"\n" );
	}
}
