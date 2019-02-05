package de.retest.recheck.execution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.LoadRecheckIgnoreUtil;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;

class RecheckDifferenceFinderTest {

	@BeforeEach
	void setUp() {
		LoadRecheckIgnoreUtil.loadRecheckIgnore();
	}

	@Test
	void find_differences_should_find_for_states() {
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

		final RecheckDifferenceFinder cut = new RecheckDifferenceFinder( null, "foo", "" );

		final ActionReplayResult differences = cut.findDifferences( actual, expected );

		assertThat( differences.hasDifferences() ).isTrue();
		assertThat( differences.getUniqueDifferences() ).hasSize( 1 );
		assertThat( differences.getStateDifference() ).isNotNull();
		assertThat( differences.getWindows() ).isEmpty();
	}

	@Test
	void find_differences_should_find_no_for_equal_states() {
		final RootElement expectedElement = mock( RootElement.class );
		final IdentifyingAttributes expectedId = IdentifyingAttributes.create( Path.fromString( "/foo[1]" ), "none" );
		when( expectedElement.getIdentifyingAttributes() ).thenReturn( expectedId );
		when( expectedElement.getAttributes() ).thenReturn( new Attributes() );

		final RootElement actualElement = mock( RootElement.class );
		final IdentifyingAttributes actualId = IdentifyingAttributes.create( Path.fromString( "/foo[1]" ), "none" );
		when( actualElement.getIdentifyingAttributes() ).thenReturn( actualId );
		when( actualElement.getAttributes() ).thenReturn( new Attributes() );

		final SutState expected = mock( SutState.class );
		when( expected.getRootElements() ).thenReturn( Collections.singletonList( expectedElement ) );

		final SutState actual = mock( SutState.class );
		when( actual.getRootElements() ).thenReturn( Collections.singletonList( actualElement ) );

		final RecheckDifferenceFinder cut = new RecheckDifferenceFinder( null, "foo", "" );

		final ActionReplayResult differences = cut.findDifferences( actual, expected );

		assertThat( differences.hasDifferences() ).isFalse();
		assertThat( differences.getUniqueDifferences() ).hasSize( 0 );
		assertThat( differences.getStateDifference() ).isNull();
		assertThat( differences.getWindows() ).isNotEmpty();
	}
}
