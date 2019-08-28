package de.retest.recheck.printer;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.LeafDifference;
import de.retest.recheck.ui.diff.StateDifference;

class TestReplayResultPrinterTest {

	TestReplayResultPrinter cut;

	@BeforeEach
	void setUp() {
		cut = new TestReplayResultPrinter( s -> ( identAttributes, key, value ) -> false );
	}

	@Test
	void toString_should_print_differences_if_empty() {
		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences() ).thenReturn( Collections.emptySet() );
		when( result.getActionReplayResults() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "Test 'null' has 0 difference(s) in 0 state(s):\n" );
	}

	@Test
	void toString_should_respect_indent_if_empty() {
		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences() ).thenReturn( Collections.emptySet() );
		when( result.getActionReplayResults() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( result, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_difference() {
		final ActionReplayResult a1 = mock( ActionReplayResult.class );
		when( a1.getDescription() ).thenReturn( "foo" );
		when( a1.getStateDifference() ).thenReturn( mock( StateDifference.class ) );

		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferencesCount() ).thenReturn( 1 );
		when( result.getActionReplayResults() ).thenReturn( singletonList( a1 ) );
		when( result.getDifferences() ).thenReturn( singleton( mock( LeafDifference.class ) ) );

		final String string = cut.toString( result );

		assertThat( string ).startsWith( "Test 'null' has 1 difference(s) in 1 state(s):" );
	}

	@Test
	void toString_should_print_difference_with_indent() {
		final ActionReplayResult a1 = mock( ActionReplayResult.class );
		when( a1.getDescription() ).thenReturn( "foo" );
		when( a1.getStateDifference() ).thenReturn( mock( StateDifference.class ) );

		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences() ).thenReturn( singleton( mock( LeafDifference.class ) ) );
		when( result.getActionReplayResults() ).thenReturn( Collections.singletonList( a1 ) );

		final String string = cut.toString( result, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_multiple_differences() {
		final ActionReplayResult a1 = mock( ActionReplayResult.class );
		when( a1.getDescription() ).thenReturn( "foo" );
		when( a1.getStateDifference() ).thenReturn( mock( StateDifference.class ) );
		final ActionReplayResult a2 = mock( ActionReplayResult.class );
		when( a2.getDescription() ).thenReturn( "bar" );
		when( a2.getStateDifference() ).thenReturn( mock( StateDifference.class ) );

		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences() ).thenReturn( singleton( mock( LeafDifference.class ) ) );
		when( result.getActionReplayResults() ).thenReturn( Arrays.asList( a1, a2 ) );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "Test 'null' has 1 difference(s) in 2 state(s):\n" );
	}

	@Test
	void toString_should_not_print_if_no_differences() {
		final ActionReplayResult emptyActionResult = mock( ActionReplayResult.class );
		when( emptyActionResult.hasDifferences() ).thenReturn( false );

		final TestReplayResult testResult = mock( TestReplayResult.class );
		when( testResult.getActionReplayResults() ).thenReturn( Collections.singletonList( emptyActionResult ) );

		assertThat( cut.toString( testResult ) ).isEqualTo( "Test 'null' has 0 difference(s) in 1 state(s):\n" );
	}

	@Test
	void toString_should_print_if_no_golden_master() {
		final RootElement rootElement = mock( RootElement.class, RETURNS_MOCKS );

		final SutState sutState = mock( SutState.class );
		when( sutState.getRootElements() ).thenReturn( Collections.singletonList( rootElement ) );

		final NoGoldenMasterActionReplayResult noGoldenMaster =
				new NoGoldenMasterActionReplayResult( "step", sutState, "path" );

		final TestReplayResult testResult = mock( TestReplayResult.class );
		when( testResult.getActionReplayResults() ).thenReturn( Collections.singletonList( noGoldenMaster ) );

		assertThat( cut.toString( testResult ) ).isNotNull();
	}
}
