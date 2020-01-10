package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.DeletedFilter;
import de.retest.recheck.review.ignore.MatcherFilter;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.GroundState;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;
import de.retest.recheck.ui.image.Screenshot;

class TestReportFilterTest {

	Filter filter;
	AttributeDifference filterMe;
	AttributeDifference notFilterMe;
	AttributesDifference attributesDiff;
	IdentifyingAttributesDifference identAttributesDiff;
	Element element;
	IdentifyingAttributes identAttributes;
	Collection<ElementDifference> childDiffs;
	ElementDifference elementDiff;
	RootElementDifference rootElementDiff;
	List<RootElementDifference> rootElementDiffs;
	StateDifference stateDiff;
	ActionReplayResult actionReplayResult;
	TestReplayResult testReplayResult;
	SuiteReplayResult suiteReplayResult;
	TestReport testReport;
	TestReportFilter cut;

	@BeforeEach
	void setUp() {
		final String keyToFilter = "filterMe";
		final String keyNotToFilter = "notFilterMe";

		filter = new CompoundFilter( new AttributeFilter( "a" ), new AttributeFilter( keyToFilter ) );

		filterMe = new AttributeDifference( keyToFilter, null, null );
		notFilterMe = new AttributeDifference( keyNotToFilter, null, null );

		element = mock( Element.class, RETURNS_DEEP_STUBS );

		identAttributes = mock( IdentifyingAttributes.class );

		final List<AttributeDifference> attributeDiffs = Arrays.asList( filterMe, notFilterMe );
		attributesDiff = new AttributesDifference( attributeDiffs );

		identAttributesDiff =
				new IdentifyingAttributesDifference( mock( IdentifyingAttributes.class ), attributeDiffs );

		final ElementDifference childDiff = new ElementDifference( element, attributesDiff, identAttributesDiff,
				mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );
		final ElementDifference otherChildDiff = new ElementDifference( element, attributesDiff, identAttributesDiff,
				mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );
		childDiffs = Arrays.asList( childDiff, otherChildDiff );

		elementDiff = new ElementDifference( element, attributesDiff, identAttributesDiff, mock( Screenshot.class ),
				mock( Screenshot.class ), childDiffs );
		when( elementDiff.getIdentifyingAttributes() ).thenReturn( identAttributes );
		when( elementDiff.getIdentifyingAttributes().identifier() ).thenReturn( "identifier" );

		rootElementDiff =
				new RootElementDifference( elementDiff, mock( RootElement.class ), mock( RootElement.class ) );
		final RootElementDifference otherRootElementDiff =
				new RootElementDifference( elementDiff, mock( RootElement.class ), mock( RootElement.class ) );

		rootElementDiffs = Arrays.asList( rootElementDiff, otherRootElementDiff );

		stateDiff = new StateDifference( rootElementDiffs );

		actionReplayResult = mock( ActionReplayResult.class );
		when( actionReplayResult.getMetadataDifference() ).thenReturn( MetadataDifference.empty() );
		when( actionReplayResult.getStateDifference() ).thenReturn( stateDiff );

		testReplayResult = new TestReplayResult( "test", 1 );
		testReplayResult.addAction( actionReplayResult );

		suiteReplayResult = new SuiteReplayResult( "", 0, mock( GroundState.class ), "", mock( GroundState.class ) );
		suiteReplayResult.addTest( testReplayResult );

		testReport = new TestReport();
		testReport.addSuite( suiteReplayResult );

		cut = new TestReportFilter( filter );
	}

	@Test
	void attributes_difference_should_be_filtered_properly() throws Exception {
		assertThat( cut.filter( mock( Element.class ), attributesDiff ) ) //
				.map( AttributesDifference::getDifferences ) //
				.hasValueSatisfying( attributesDiff -> assertThat( attributesDiff ).containsExactly( notFilterMe ) );
	}

	@Test
	void attributes_difference_should_be_null_when_all_differences_are_filtered() throws Exception {
		final AttributesDifference attributesDiff = new AttributesDifference( Collections.singletonList( filterMe ) );
		assertThat( cut.filter( element, attributesDiff ) ).isEmpty();
	}

	@Test
	void identifying_attributes_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );
		assertThat( cut.filter( element, identAttributesDiff ) ) //
				.map( IdentifyingAttributesDifference::getAttributeDifferences ) //
				.hasValueSatisfying( attributeDiffs -> assertThat( attributeDiffs ).containsExactly( notFilterMe ) );
	}

	@Test
	void identifying_attributes_differences_should_be_null_when_all_differences_are_filtered() throws Exception {
		final IdentifyingAttributes expectedIdentAttributes = mock( IdentifyingAttributes.class );

		final List<AttributeDifference> attributeDiffs = Collections.singletonList( filterMe );

		final IdentifyingAttributesDifference identAttributesDiff =
				new IdentifyingAttributesDifference( expectedIdentAttributes, attributeDiffs );

		assertThat( cut.filter( element, identAttributesDiff ) ).isEmpty();
	}

	@Test
	void inserted_deleted_differences_should_be_filtered_properly() throws Exception {
		final InsertedDeletedElementDifference insertedDiff =
				InsertedDeletedElementDifference.differenceFor( null, element );
		final InsertedDeletedElementDifference deletedDiff =
				InsertedDeletedElementDifference.differenceFor( element, null );

		assertThat( new TestReportFilter( filter ).filter( insertedDiff ) ).hasValue( insertedDiff );
		assertThat( new TestReportFilter( filter ).filter( deletedDiff ) ).hasValue( deletedDiff );

		final Filter elementFilter = new MatcherFilter( e -> e == element );

		assertThat( new TestReportFilter( elementFilter ).filter( insertedDiff ) ).isEmpty();
		assertThat( new TestReportFilter( elementFilter ).filter( deletedDiff ) ).isEmpty();

		final Filter deletedFilter = new DeletedFilter();

		assertThat( new TestReportFilter( deletedFilter ).filter( insertedDiff ) ).hasValue( insertedDiff );
		assertThat( new TestReportFilter( deletedFilter ).filter( deletedDiff ) ).isEmpty();
	}

	@Test
	void collection_of_element_differences_should_be_filtered_properly() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( identAttributes );
		final Collection<ElementDifference> filteredChildDifferences = cut.filter( childDiffs );
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

		assertThat( cut.filter( elementDiff ) ).hasValueSatisfying( fiteredElementDiff -> {
			assertThat( fiteredElementDiff.getAttributesDifference().getDifferences() ).containsExactly( notFilterMe );
			final List<ElementDifference> childElementDiffs =
					new ArrayList<>( fiteredElementDiff.getChildDifferences() );
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
		when( filterAll.matches( any(), any( AttributeDifference.class ) ) ).thenReturn( true );

		final TestReportFilter cut = new TestReportFilter( filterAll );

		final Optional<ElementDifference> filteredElementDifference = cut.filter( difference );

		assertThat( filteredElementDifference ).isEmpty();
	}

	@Test
	void element_difference_should_be_preserved_if_child_differences() throws Exception {
		final Element child = mock( Element.class );
		final ElementDifference childWithOwnDiffs =
				new ElementDifference( child, attributesDiff, null, null, null, Collections.emptyList() );

		final Element parent = mock( Element.class );
		final ElementDifference parentWithoutOwnDiffs =
				new ElementDifference( parent, null, null, null, null, Collections.singletonList( childWithOwnDiffs ) );

		final Filter childFilter = new MatcherFilter( e -> e == child );
		final Filter parentFilter = new MatcherFilter( e -> e == parent );

		assertThat( new TestReportFilter( childFilter ).filter( childWithOwnDiffs ) ).isEmpty();
		assertThat( new TestReportFilter( parentFilter ).filter( childWithOwnDiffs ) ).isNotEmpty();
		assertThat( new TestReportFilter( parentFilter ).filter( parentWithoutOwnDiffs ) ).isNotEmpty();
	}

	@Test
	void inserted_deleted_element_difference_should_be_filtered() throws Exception {
		final Element deletedChild = mock( Element.class, RETURNS_DEEP_STUBS );
		when( deletedChild.getIdentifyingAttributes().getPath() ).thenReturn( "parent[1]/child[1]" );
		final InsertedDeletedElementDifference deletedDiff =
				InsertedDeletedElementDifference.differenceFor( deletedChild, null );
		final ElementDifference deletedElementDiff =
				new ElementDifference( deletedChild, null, deletedDiff, null, null, Collections.emptyList() );

		final Element insertedChild = mock( Element.class, RETURNS_DEEP_STUBS );
		when( insertedChild.getIdentifyingAttributes().getPath() ).thenReturn( "parent[1]/child[2]" );
		final InsertedDeletedElementDifference insertedDiff =
				InsertedDeletedElementDifference.differenceFor( null, insertedChild );
		final ElementDifference insertedElementDiff =
				new ElementDifference( insertedChild, null, insertedDiff, null, null, Collections.emptyList() );

		final Element parent = mock( Element.class, RETURNS_DEEP_STUBS );
		when( parent.getIdentifyingAttributes().getPath() ).thenReturn( "parent[1]" );
		final ElementDifference parentDiff = new ElementDifference( parent, null, null, null, null,
				Arrays.asList( deletedElementDiff, insertedElementDiff ) );

		when( deletedChild.getParent() ).thenReturn( parent );
		when( insertedChild.getParent() ).thenReturn( parent );

		final Filter parentXPathFilter = new MatcherFilter( new ElementXPathMatcher( parent ) );

		assertThat( parentXPathFilter.matches( parent ) ).isTrue();
		assertThat( parentXPathFilter.matches( deletedChild ) ).isTrue();
		assertThat( parentXPathFilter.matches( insertedChild ) ).isTrue();
		assertThat( new TestReportFilter( parentXPathFilter ).filter( parentDiff ) ).isEmpty();
	}

	@Test
	void root_element_difference_should_be_filtered_properly() throws Exception {
		when( elementDiff.getIdentifyingAttributes() ).thenReturn( identAttributes );
		when( elementDiff.getIdentifyingAttributes().identifier() ).thenReturn( "identifier" );
		assertThat( cut.filter( rootElementDiff ) )
				.map( filteredRootElementDiff -> filteredRootElementDiff.getElementDifference() //
						.getAttributesDifference() //
						.getDifferences() )
				.hasValueSatisfying( attributeDiffs -> assertThat( attributeDiffs ).containsExactly( notFilterMe ) );
	}

	@Test
	void list_of_root_element_differences_should_be_filtered_properly() throws Exception {
		final List<RootElementDifference> filteredRootElementDifferences = cut.filter( rootElementDiffs );
		final List<AttributeDifference> differences = filteredRootElementDifferences.get( 0 ).getElementDifference()
				.getAttributesDifference().getDifferences();
		assertThat( differences ).contains( notFilterMe );
	}

	@Test
	void state_difference_should_be_filtered_properly() throws Exception {
		final StateDifference filteredStateDifference = cut.filter( stateDiff );
		final List<AttributeDifference> differences = filteredStateDifference.getRootElementDifferences().get( 0 )
				.getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void state_difference_should_not_throw_if_null() {
		final StateDifference empty = null; // This is the cause
		final TestReportFilter cut = new TestReportFilter( mock( Filter.class ) );
		assertThatCode( () -> cut.filter( empty ) ).doesNotThrowAnyException();
	}

	@Test
	void state_difference_should_not_destroy_if_no_difference() {
		final StateDifference empty = null; // This is the cause
		final StateDifference difference = mock( StateDifference.class );
		final TestReportFilter cut = new TestReportFilter( mock( Filter.class ) );

		assertThat( cut.filter( empty ) ).isEqualTo( empty );
		assertThat( cut.filter( difference ) ).isEqualTo( difference );
	}

	@Test
	void state_difference_should_have_no_root_element_differences_when_all_differences_are_filtered() throws Exception {
		final List<AttributeDifference> attributeDifferences = Collections.singletonList( filterMe );

		final AttributesDifference attributesDiff = new AttributesDifference( attributeDifferences );

		final ElementDifference elementDiff = new ElementDifference( element, attributesDiff, null,
				mock( Screenshot.class ), mock( Screenshot.class ), Collections.emptyList() );

		final RootElementDifference rootElementDiff =
				new RootElementDifference( elementDiff, mock( RootElement.class ), mock( RootElement.class ) );

		final List<RootElementDifference> rootElementDiffs = Collections.singletonList( rootElementDiff );

		final StateDifference stateDiff = new StateDifference( rootElementDiffs );

		assertThat( stateDiff.getRootElementDifferences() ).isNotEmpty();

		final StateDifference filteredStateDiff = cut.filter( stateDiff );

		assertThat( filteredStateDiff.getRootElementDifferences() ).isEmpty();
	}

	@Test
	void action_replay_result_should_be_filtered_properly() throws Exception {
		final ActionReplayResult filteredActionReplayResult = cut.filter( actionReplayResult );
		final List<AttributeDifference> differences = filteredActionReplayResult.getStateDifference()
				.getRootElementDifferences().get( 0 ).getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void action_replay_result_should_not_throw_if_difference_is_null() {
		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getStateDifference() ).thenReturn( null ); // Just to make sure that this is the cause
		when( result.getMetadataDifference() ).thenReturn( MetadataDifference.empty() );
		final TestReportFilter cut = new TestReportFilter( mock( Filter.class ) );
		assertThatCode( () -> cut.filter( result ) ).doesNotThrowAnyException();
	}

	@Test
	void action_replay_result_should_keep_golden_master_exceptions_even_if_filtered() {
		final ActionReplayResult noGoldenMasterActionResult = mock( NoGoldenMasterActionReplayResult.class );

		final Filter noFilter = mock( Filter.class );
		final Filter doFilter = mock( Filter.class );
		when( doFilter.matches( any(), any( AttributeDifference.class ) ) ).thenReturn( true );
		when( doFilter.matches( any() ) ).thenReturn( true );

		assertThat( new TestReportFilter( noFilter ).filter( noGoldenMasterActionResult ) )
				.isEqualTo( noGoldenMasterActionResult );
		assertThat( new TestReportFilter( doFilter ).filter( noGoldenMasterActionResult ) )
				.isEqualTo( noGoldenMasterActionResult );
	}

	@Test
	void test_replay_result_should_be_filtered_properly() throws Exception {
		final TestReplayResult filteredTestReplayResult = cut.filter( testReplayResult );
		final List<AttributeDifference> differences = filteredTestReplayResult.getActionReplayResults().get( 0 )
				.getStateDifference().getRootElementDifferences().get( 0 ).getElementDifference()
				.getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void suite_replay_result_should_be_filtered_properly() throws Exception {
		final SuiteReplayResult filteredSuiteReplayResult = cut.filter( suiteReplayResult );
		final StateDifference stateDifference = filteredSuiteReplayResult.getTestReplayResults().get( 0 )
				.getActionReplayResults().get( 0 ).getStateDifference();
		final List<AttributeDifference> differences = stateDifference.getRootElementDifferences().get( 0 )
				.getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void test_report_should_be_filtered_properly() throws Exception {
		final TestReport filteredTestReport = cut.filter( testReport );
		final SuiteReplayResult suiteReplayResult = filteredTestReport.getSuiteReplayResults().get( 0 );
		final ActionReplayResult actionReplayResult =
				suiteReplayResult.getTestReplayResults().get( 0 ).getActionReplayResults().get( 0 );
		final List<AttributeDifference> differences = actionReplayResult.getStateDifference()
				.getRootElementDifferences().get( 0 ).getElementDifference().getAttributesDifference().getDifferences();
		assertThat( differences ).containsExactly( notFilterMe );
	}

	@Test
	void filter_should_not_touch_metadata_if_no_filter_matches() {
		// the attribute 'meta-a' is NOT filtered
		final MetadataElementDifference difference = new MetadataElementDifference( "meta-a", "b", "c" );
		when( actionReplayResult.getMetadataDifference() )
				.thenReturn( MetadataDifference.of( Collections.singleton( difference ) ) );

		final ActionReplayResult filtered = cut.filter( actionReplayResult );

		assertThat( filtered.getMetadataDifference() ).isEqualTo( actionReplayResult.getMetadataDifference() );
	}

	@Test
	void filter_should_recreate_metadata_if_filter_matches() {
		// the attribute 'a' is filtered
		final MetadataElementDifference difference = new MetadataElementDifference( "a", "b", "c" );
		when( actionReplayResult.getMetadataDifference() )
				.thenReturn( MetadataDifference.of( Collections.singleton( difference ) ) );

		final ActionReplayResult filtered = cut.filter( actionReplayResult );

		assertThat( filtered.getMetadataDifference() ).isEmpty();
	}

	@Test
	void filter_should_not_alter_test_source_root_from_suite( @TempDir final Path path ) {
		final SuiteReplayResult noTestSourceRoot = mock( SuiteReplayResult.class );
		when( noTestSourceRoot.getTestSourceRoot() ).thenReturn( Optional.empty() );

		assertThat( cut.filter( noTestSourceRoot ).getTestSourceRoot() ).isEmpty();

		final SuiteReplayResult testSourceRoot = mock( SuiteReplayResult.class );
		when( testSourceRoot.getTestSourceRoot() ).thenReturn( Optional.of( path ) );

		assertThat( cut.filter( testSourceRoot ).getTestSourceRoot() ).hasValue( path );
	}
}
