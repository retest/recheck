package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.GroundState;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.DurationDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.image.Screenshot;

class TestReportFilterTest {

	Filter filter;
	AttributeDifference filterMe;
	AttributeDifference notFilterMe;
	AttributesDifference originalAttributesDifference;
	IdentifyingAttributesDifference originalIdentAttributesDifference;
	Element element;
	IdentifyingAttributes identAttributes;
	Collection<ElementDifference> childDifferences;
	ElementDifference originalElementDifference;
	RootElementDifference originalRootElementDifference;
	List<RootElementDifference> originalRootElementDifferences;
	StateDifference originalStateDifference;
	ActionReplayResult originalActionReplayResult;
	TestReplayResult originalTestReplayResult;
	SuiteReplayResult originalSuiteReplayResult;
	TestReport originalTestReport;

	@BeforeEach
	void setUp() {
		final String keyToFilter = "filterMe";
		final String keyNotToFilter = "notFilterMe";
		filter = new AttributeFilter( keyToFilter );
		filterMe = new AttributeDifference( keyToFilter, null, null );
		notFilterMe = new AttributeDifference( keyNotToFilter, null, null );
		element = mock( Element.class );
		identAttributes = mock( IdentifyingAttributes.class );
		final List<AttributeDifference> attributeDifferences = Arrays.asList( filterMe, notFilterMe );
		originalAttributesDifference = new AttributesDifference( attributeDifferences );
		originalIdentAttributesDifference =
				new IdentifyingAttributesDifference( mock( IdentifyingAttributes.class ), attributeDifferences );
		final ElementDifference firstChildDifference =
				new ElementDifference( element, originalAttributesDifference, originalIdentAttributesDifference,
						mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );
		final ElementDifference secondChildDifference =
				new ElementDifference( element, originalAttributesDifference, originalIdentAttributesDifference,
						mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );
		childDifferences = Arrays.asList( firstChildDifference, secondChildDifference );
		originalElementDifference =
				new ElementDifference( element, originalAttributesDifference, originalIdentAttributesDifference,
						mock( Screenshot.class ), mock( Screenshot.class ), childDifferences );
		when( originalElementDifference.getIdentifyingAttributes() ).thenReturn( identAttributes );
		when( originalElementDifference.getIdentifyingAttributes().identifier() ).thenReturn( "identifier" );
		originalRootElementDifference = new RootElementDifference( originalElementDifference, mock( RootElement.class ),
				mock( RootElement.class ) );
		final RootElementDifference secondRootElementDifference = new RootElementDifference( originalElementDifference,
				mock( RootElement.class ), mock( RootElement.class ) );
		originalRootElementDifferences = Arrays.asList( originalRootElementDifference, secondRootElementDifference );
		originalStateDifference =
				new StateDifference( originalRootElementDifferences, mock( DurationDifference.class ) );
		originalActionReplayResult = mock( ActionReplayResult.class );
		when( originalActionReplayResult.getStateDifference() ).thenReturn( originalStateDifference );
		originalTestReplayResult = new TestReplayResult( "test", 1 );
		originalTestReplayResult.addAction( originalActionReplayResult );
		originalSuiteReplayResult =
				new SuiteReplayResult( "", 0, mock( GroundState.class ), "", mock( GroundState.class ) );
		originalSuiteReplayResult.addTest( originalTestReplayResult );
		originalTestReport = new TestReport();
		originalTestReport.addSuite( originalSuiteReplayResult );
	}

	@Test
	void attributes_differences_should_be_filtered_properly() throws Exception {
		final AttributesDifference filtered =
				TestReportFilter.filter( mock( Element.class ), originalAttributesDifference, filter );
		assertThat( filtered.getDifferences() ).containsExactly( notFilterMe );
	}

	@Test
	void attributes_differences_should_be_null_when_all_differences_are_filtered() throws Exception {
		final AttributesDifference attributesDiff = new AttributesDifference( Arrays.asList( filterMe ) );
		assertThat( TestReportFilter.filter( element, attributesDiff, filter ) ).isNull();
	}

	@Test
	void identifying_attributes_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		final IdentifyingAttributesDifference filtered =
				TestReportFilter.filter( element, originalIdentAttributesDifference, filter );
		assertThat( filtered.getAttributeDifferences() ).containsExactly( notFilterMe );
	}

	@Test
	void identifying_attributes_differences_should_be_null_when_all_differences_are_filtered() throws Exception {
		final IdentifyingAttributes expectedIdentAttributes = mock( IdentifyingAttributes.class );

		final List<AttributeDifference> attributeDiffs = Arrays.asList( filterMe );

		final IdentifyingAttributesDifference identAttributesDiff =
				new IdentifyingAttributesDifference( expectedIdentAttributes, attributeDiffs );

		assertThat( TestReportFilter.filter( element, identAttributesDiff, filter ) ).isNull();
	}

	@Test
	void collection_of_element_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( identAttributes );
		final Collection<ElementDifference> filteredChildDifferences =
				TestReportFilter.filter( childDifferences, filter );
		final List<ElementDifference> elementDifferences =
				filteredChildDifferences.stream().collect( Collectors.toList() );
		assertThat( elementDifferences.get( 0 ).getAttributesDifference().getDifferences() )
				.containsExactly( notFilterMe );
		assertThat( elementDifferences.get( 1 ).getAttributesDifference().getDifferences() )
				.containsExactly( notFilterMe );
	}

	@Test
	void element_difference_and_child_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( identAttributes );

		assertThat( TestReportFilter.filter( originalElementDifference, filter ) )
				.hasValueSatisfying( fiteredElementDiff -> {
					assertThat( fiteredElementDiff.getAttributesDifference().getDifferences() )
							.containsExactly( notFilterMe );
					final List<ElementDifference> childElementDiffs = fiteredElementDiff.getChildDifferences().stream() //
							.collect( Collectors.toList() );
					assertThat( childElementDiffs.get( 0 ).getAttributesDifference().getDifferences() )
							.containsExactly( notFilterMe );
				} );
	}

	@Test
	void filter_element_difference_should_have_no_differences_if_filtered() {
		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		final AttributesDifference attributes = mock( AttributesDifference.class );
		when( attributes.getDifferences() ).thenReturn( Collections.singletonList( attributeDifference ) );

		final ElementDifference difference = mock( ElementDifference.class );
		when( difference.getAttributesDifference() ).thenReturn( attributes );

		final Filter filterAll = mock( Filter.class );
		when( filterAll.matches( any() ) ).thenReturn( true );
		when( filterAll.matches( any(), any() ) ).thenReturn( true );

		final Optional<ElementDifference> filteredElementDifference = TestReportFilter.filter( difference, filterAll );

		assertThat( filteredElementDifference ).isEmpty();
	}

	@Test
	void root_element_difference_should_be_filtered_properly() throws Exception {
		when( originalElementDifference.getIdentifyingAttributes() ).thenReturn( identAttributes );
		when( originalElementDifference.getIdentifyingAttributes().identifier() ).thenReturn( "identifier" );
		assertThat( TestReportFilter.filter( originalRootElementDifference, filter ) )
				.map( filteredRootElementDiff -> filteredRootElementDiff.getElementDifference() //
						.getAttributesDifference() //
						.getDifferences() )
				.hasValueSatisfying( attributeDiffs -> assertThat( attributeDiffs ).containsExactly( notFilterMe ) );
	}

	@Test
	void list_of_root_element_differences_should_be_filtered_properly() throws Exception {
		final List<RootElementDifference> filteredRootElementDifferences =
				TestReportFilter.filter( originalRootElementDifferences, filter );
		final List<AttributeDifference> differences = filteredRootElementDifferences.get( 0 ).getElementDifference()
				.getAttributesDifference().getDifferences();
		assertThat( differences ).contains( notFilterMe );
	}

	@Test
	void state_difference_should_be_filtered_properly() throws Exception {
		final StateDifference filteredStateDifference = TestReportFilter.filter( originalStateDifference, filter );
		final List<AttributeDifference> differences = filteredStateDifference.getRootElementDifferences().get( 0 )
				.getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void state_difference_should_not_throw_if_null() {
		final StateDifference empty = null; // This is the cause
		assertThatCode( () -> TestReportFilter.filter( empty, mock( Filter.class ) ) ).doesNotThrowAnyException();
	}

	@Test
	void state_difference_should_not_destroy_if_no_difference() {
		final StateDifference empty = null; // This is the cause
		final StateDifference difference = mock( StateDifference.class );

		assertThat( TestReportFilter.filter( empty, mock( Filter.class ) ) ).isEqualTo( empty );
		assertThat( TestReportFilter.filter( difference, mock( Filter.class ) ) ).isEqualTo( difference );
	}

	@Test
	void state_difference_should_have_no_root_element_differences_when_all_differences_are_filtered() throws Exception {
		final List<AttributeDifference> attributeDifferences = Arrays.asList( filterMe );

		final AttributesDifference attributesDiff = new AttributesDifference( attributeDifferences );

		final ElementDifference elementDiff = new ElementDifference( element, attributesDiff, null,
				mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );

		final RootElementDifference rootElementDiff =
				new RootElementDifference( elementDiff, mock( RootElement.class ), mock( RootElement.class ) );

		final List<RootElementDifference> rootElementDiffs = Arrays.asList( rootElementDiff );

		final DurationDifference durationDiff = DurationDifference.differenceFor( 0L, 0L );

		final StateDifference stateDiff = new StateDifference( rootElementDiffs, durationDiff );

		assertThat( stateDiff.getRootElementDifferences() ).isNotEmpty();

		final StateDifference filteredStateDiff = TestReportFilter.filter( stateDiff, filter );

		assertThat( filteredStateDiff.getRootElementDifferences() ).isEmpty();
	}

	@Test
	void action_replay_result_should_be_filtered_properly() throws Exception {
		final ActionReplayResult filteredActionReplayResult =
				TestReportFilter.filter( originalActionReplayResult, filter );
		final List<AttributeDifference> differences = filteredActionReplayResult.getStateDifference()
				.getRootElementDifferences().get( 0 ).getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void action_replay_result_should_not_throw_if_null() {
		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getStateDifference() ).thenReturn( null ); // Just to make sure that this is the cause

		assertThatCode( () -> TestReportFilter.filter( result, mock( Filter.class ) ) ).doesNotThrowAnyException();
	}

	@Test
	void action_replay_result_should_keep_golden_master_exceptions_even_if_filtered() {
		final ActionReplayResult noGoldenMasterActionResult = mock( NoGoldenMasterActionReplayResult.class );

		final Filter noFilter = mock( Filter.class );
		final Filter doFilter = mock( Filter.class );
		when( doFilter.matches( any(), any() ) ).thenReturn( true );
		when( doFilter.matches( any() ) ).thenReturn( true );

		assertThat( TestReportFilter.filter( noGoldenMasterActionResult, noFilter ) )
				.isEqualTo( noGoldenMasterActionResult );
		assertThat( TestReportFilter.filter( noGoldenMasterActionResult, doFilter ) )
				.isEqualTo( noGoldenMasterActionResult );
	}

	@Test
	void test_replay_result_should_be_filtered_properly() throws Exception {
		final TestReplayResult filteredTestReplayResult = TestReportFilter.filter( originalTestReplayResult, filter );
		final List<AttributeDifference> differences = filteredTestReplayResult.getActionReplayResults().get( 0 )
				.getStateDifference().getRootElementDifferences().get( 0 ).getElementDifference()
				.getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void suite_replay_result_should_be_filtered_properly() throws Exception {
		final SuiteReplayResult filteredSuiteReplayResult =
				TestReportFilter.filter( originalSuiteReplayResult, filter );
		final StateDifference stateDifference = filteredSuiteReplayResult.getTestReplayResults().get( 0 )
				.getActionReplayResults().get( 0 ).getStateDifference();
		final List<AttributeDifference> differences = stateDifference.getRootElementDifferences().get( 0 )
				.getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void test_report_should_be_filtered_properly() throws Exception {
		final TestReport filteredTestReport = TestReportFilter.filter( originalTestReport, filter );
		final SuiteReplayResult suiteReplayResult = filteredTestReport.getSuiteReplayResults().get( 0 );
		final ActionReplayResult actionReplayResult =
				suiteReplayResult.getTestReplayResults().get( 0 ).getActionReplayResults().get( 0 );
		final List<AttributeDifference> differences = actionReplayResult.getStateDifference()
				.getRootElementDifferences().get( 0 ).getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}
}
