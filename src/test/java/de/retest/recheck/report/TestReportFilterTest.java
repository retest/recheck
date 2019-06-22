package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		final AttributeDifference filterMe = new AttributeDifference( keyToFilter, null, null );
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
	void Attributes_differences_should_be_filtered_properly() throws Exception {
		final AttributesDifference filtered =
				TestReportFilter.filter( mock( Element.class ), originalAttributesDifference, filter );
		assertThat( filtered.getDifferences() ).containsExactly( notFilterMe );
	}

	@Test
	void identifying_attributes_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		final IdentifyingAttributesDifference filtered =
				TestReportFilter.filter( element, originalIdentAttributesDifference, filter );
		assertThat( filtered.getAttributeDifferences() ).containsExactly( notFilterMe );
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
		final ElementDifference filteredElementDifference =
				TestReportFilter.filter( originalElementDifference, filter );
		final List<ElementDifference> childElementDiffferences =
				filteredElementDifference.getChildDifferences().stream()//
						.collect( Collectors.toList() );
		assertThat( filteredElementDifference.getAttributesDifference().getDifferences() )
				.containsExactly( notFilterMe );
		assertThat( childElementDiffferences.get( 0 ).getAttributesDifference().getDifferences() )
				.containsExactly( notFilterMe );
	}

	@Test
	void root_element_difference_should_be_filtered_properly() throws Exception {
		when( originalElementDifference.getIdentifyingAttributes() ).thenReturn( identAttributes );
		when( originalElementDifference.getIdentifyingAttributes().identifier() ).thenReturn( "identifier" );
		final RootElementDifference filteredRootElementDifference =
				TestReportFilter.filter( originalRootElementDifference, filter );
		final List<AttributeDifference> differences =
				filteredRootElementDifference.getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
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
	void action_replay_result_should_be_filtered_properly() throws Exception {
		final ActionReplayResult filteredActionReplayResult =
				TestReportFilter.filter( originalActionReplayResult, filter );
		final List<AttributeDifference> differences = filteredActionReplayResult.getStateDifference()
				.getRootElementDifferences().get( 0 ).getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
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
