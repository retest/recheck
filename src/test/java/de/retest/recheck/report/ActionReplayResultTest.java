package de.retest.recheck.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.ui.diff.StateDifference;

class ActionReplayResultTest {

	@Test
	void getDiffrences_should_ignore_path_changes() {
		final RootElement expectedElement = mock( RootElement.class );
		final IdentifyingAttributes expectedId = IdentifyingAttributes.create( Path.fromString( "/foo[1]" ), "none" );
		when( expectedElement.getIdentifyingAttributes() ).thenReturn( expectedId );
		when( expectedElement.getAttributes() ).thenReturn( new Attributes() );

		final RootElement actualElement = mock( RootElement.class );
		final IdentifyingAttributes actualId = IdentifyingAttributes.create( Path.fromString( "/bar[1]" ), "none" );
		when( actualElement.getIdentifyingAttributes() ).thenReturn( actualId );
		when( actualElement.getAttributes() ).thenReturn( new Attributes() );

		final SutState expected = mock( SutState.class );
		when( expected.getRootElements() ).thenReturn( Collections.singletonList( expectedElement ) );

		final SutState actual = mock( SutState.class );
		when( actual.getRootElements() ).thenReturn( Collections.singletonList( actualElement ) );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );
		final RecheckDifferenceFinder cut = new RecheckDifferenceFinder( dvf, "foo", "" );

		final ActionReplayResult differences = cut.findDifferences( actual, expected );

		assertThat( differences.hasDifferences() ).isTrue();
		assertThat( differences.getDifferences() ).hasSize( 1 );
		assertThat( differences.getStateDifference() ).isNotNull();
		assertThat( differences.getWindows() ).isEmpty();
	}

	@Test
	void getDiffrences_should_ignore_attributes_changes() {
		final MutableAttributes expectedAttributes = new MutableAttributes();
		expectedAttributes.put( "font", "Lucida Grance-italic-123" );
		expectedAttributes.put( "background", "#FF0000" );
		expectedAttributes.put( "foreground", "#00FF00" );

		final MutableAttributes actualAttributes = new MutableAttributes();
		actualAttributes.put( "font", "Arial" );
		actualAttributes.put( "background", "#FFFFFF" );
		actualAttributes.put( "foreground", "#000000" );

		final RootElement expectedElement = mock( RootElement.class );
		final IdentifyingAttributes expectedId = IdentifyingAttributes.create( Path.fromString( "/foo[1]" ), "none" );
		when( expectedElement.getIdentifyingAttributes() ).thenReturn( expectedId );
		when( expectedElement.getAttributes() ).thenReturn( expectedAttributes.immutable() );

		final RootElement actualElement = mock( RootElement.class );
		final IdentifyingAttributes actualId = IdentifyingAttributes.create( Path.fromString( "/bar[1]" ), "none" );
		when( actualElement.getIdentifyingAttributes() ).thenReturn( actualId );
		when( actualElement.getAttributes() ).thenReturn( actualAttributes.immutable() );

		final SutState expected = mock( SutState.class );
		when( expected.getRootElements() ).thenReturn( Collections.singletonList( expectedElement ) );

		final SutState actual = mock( SutState.class );
		when( actual.getRootElements() ).thenReturn( Collections.singletonList( actualElement ) );

		final DefaultValueFinder dvf = Mockito.mock( DefaultValueFinder.class );
		final RecheckDifferenceFinder cut = new RecheckDifferenceFinder( dvf, "foo", "" );

		final ActionReplayResult differences = cut.findDifferences( actual, expected );

		final Filter filterAll = mock( Filter.class );
		when( filterAll.matches( expectedElement ) ).thenReturn( true );

		assertThat( differences.hasDifferences() ).isTrue();

		assertThat( differences.getDifferences() ).hasSize( 4 );

		assertThat( differences.getStateDifference() ).isNotNull();
		assertThat( differences.getWindows() ).isEmpty();
	}

	@Test
	void factory_method_should_return_no_difference_if_state_is_null() {
		final ActionReplayData data = mock( ActionReplayData.class );
		final SutState sutState = mock( SutState.class );
		final StateDifference difference = null; // This difference should be null vs empty
		final ActionReplayResult result =
				ActionReplayResult.createActionReplayResult( data, null, null, difference, 0L, sutState );

		assertThat( result.hasDifferences() ).isFalse();
	}

	@Test
	void factory_method_should_return_no_difference_if_state_is_empty() {
		final ActionReplayData data = mock( ActionReplayData.class );
		final SutState sutState = mock( SutState.class );
		final StateDifference difference = mock( StateDifference.class ); // This difference should be empty vs null
		final ActionReplayResult result =
				ActionReplayResult.createActionReplayResult( data, null, null, difference, 0L, sutState );

		assertThat( result.hasDifferences() ).isFalse();
	}

	@Test
	void getDifferences_should_retrieve_all_child_differences() {
		final List<ElementDifference> empty = Collections.emptyList();

		final List<RootElementDifference> differences = Arrays.asList( //
				root( difference( 5, Arrays.asList( //
						difference( 3, Arrays.asList( //
								difference( 2, empty ), //
								difference( 1, empty ) //
						) ), //
						difference( 4, empty ) //
				) ) ), //
				root( difference( 2, Arrays.asList( //
						difference( 1, empty ), //
						difference( 3, empty ) //
				) ) ) );

		final ActionReplayResult cut = ActionReplayResult.withDifference( ActionReplayData.ofSutStart(),
				WindowRetriever.empty(), DifferenceRetriever.of( differences ), 0L );

		assertThat( cut.getDifferences() ).hasSize( 21 );
	}

	RootElementDifference root( final ElementDifference difference ) {
		return new RootElementDifference( difference, mock( RootElement.class ), mock( RootElement.class ) );
	}

	ElementDifference difference( final int cnt, final List<ElementDifference> children ) {
		final AttributesDifference attributes = mock( AttributesDifference.class );
		when( attributes.getDifferences() ).thenReturn( Stream.generate( () -> mock( AttributeDifference.class ) )
				.limit( cnt ).collect( Collectors.toList() ) );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		return new ElementDifference( element, attributes, null, null, null, children );
	}
}
