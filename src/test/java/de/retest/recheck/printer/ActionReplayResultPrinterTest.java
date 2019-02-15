package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.NoRecheckFileActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.ui.actions.ExceptionWrapper;
import de.retest.recheck.ui.actions.TargetNotFoundException;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.ElementDifference;

class ActionReplayResultPrinterTest {

	ActionReplayResultPrinter cut;

	@BeforeEach
	void setUp() {
		cut = new ActionReplayResultPrinter( ( identifyingAttributes, attributeKey, attributeValue ) -> false );
	}

	@Test
	void toString_with_error_should_print_error() {
		final ExceptionWrapper exception = mock( ExceptionWrapper.class );
		when( exception.toString() ).thenReturn( "error" );

		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getDescription() ).thenReturn( "foo" );
		when( result.getThrowableWrapper() ).thenReturn( exception );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "foo resulted in:\n\terror" );
	}

	@Test
	void toString_with_target_not_found_should_print_error() {
		final TargetNotFoundException exception = mock( TargetNotFoundException.class );
		when( exception.toString() ).thenReturn( "tnfe" );

		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getDescription() ).thenReturn( "foo" );
		when( result.getTargetNotFoundException() ).thenReturn( exception );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "foo resulted in:\n\ttnfe" );
	}

	@Test
	void toString_with_no_exception_should_print_differences() {
		final IdentifyingAttributes mock = mock( IdentifyingAttributes.class );
		when( mock.toString() ).thenReturn( "Identifying" );
		when( mock.getPath() ).thenReturn( "path/to/element" );

		final ElementDifference childDifference = mock( ElementDifference.class );
		when( childDifference.getIdentifyingAttributes() ).thenReturn( mock );

		final ElementDifference rootDifference = mock( ElementDifference.class );
		when( rootDifference.getElementDifferences() ).thenReturn( Collections.singletonList( childDifference ) );
		when( rootDifference.getIdentifyingAttributes() ).thenReturn( mock );

		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getDescription() ).thenReturn( "foo" );
		when( result.getElementDifferences() ).thenReturn( Collections.singletonList( rootDifference ) );

		final String string = cut.toString( result );

		assertThat( string ).startsWith( "foo resulted in:\n\tIdentifying at 'path/to/element':\n\t\tnoDifferences" );
	}

	@Test
	void toString_should_respect_indent() {
		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getElementDifferences() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( result, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_message_if_no_recheck_action_replay_result() {
		final RootElement element = mock( RootElement.class );
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		final SutState state = mock( SutState.class );
		when( state.getRootElements() ).thenReturn( Collections.singletonList( element ) );

		final NoRecheckFileActionReplayResult result = new NoRecheckFileActionReplayResult( "foo", state );

		final String string = cut.toString( result );

		assertThat( string ).contains(
				"No recheck file found. First time test was run? Created recheck file now, don't forget to commit..." );
	}
}
