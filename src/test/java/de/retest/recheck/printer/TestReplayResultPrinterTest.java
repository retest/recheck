package de.retest.recheck.printer;

import static de.retest.recheck.ignore.Filter.FILTER_NOTHING;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
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
		when( result.getDifferences( FILTER_NOTHING ) ).thenReturn( Collections.emptySet() );
		when( result.getActionReplayResults() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "Test 'null' has 0 difference(s) in 0 state(s):\n" );
	}

	@Test
	void toString_should_respect_indent_if_empty() {
		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences( FILTER_NOTHING ) ).thenReturn( Collections.emptySet() );
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
		when( result.getDifferences( Mockito.any() ) ).thenReturn( singleton( mock( LeafDifference.class ) ) );

		final String string = cut.toString( result );

		assertThat( string ).startsWith( "Test 'null' has 1 difference(s) in 1 state(s):\nfoo resulted in:\n" );
	}

	@Test
	void toString_should_print_difference_with_indent() {
		final ActionReplayResult a1 = mock( ActionReplayResult.class );
		when( a1.getDescription() ).thenReturn( "foo" );
		when( a1.getStateDifference() ).thenReturn( mock( StateDifference.class ) );

		final TestReplayResult result = mock( TestReplayResult.class );
		when( result.getDifferences( FILTER_NOTHING ) ).thenReturn( singleton( mock( LeafDifference.class ) ) );
		when( result.getActionReplayResults() ).thenReturn( Collections.singletonList( a1 ) );

		final String string = cut.toString( result, "____" );

		assertThat( string ).startsWith( "____" );
		assertThat( string ).contains( "\n____" );
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
		when( result.getDifferences( FILTER_NOTHING ) ).thenReturn( singleton( mock( LeafDifference.class ) ) );
		when( result.getActionReplayResults() ).thenReturn( Arrays.asList( a1, a2 ) );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo(
				"Test 'null' has 1 difference(s) in 2 state(s):\nfoo resulted in:\n\tno differences\nbar resulted in:\n\tno differences" );
	}
}
