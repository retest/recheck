package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.actions.ExceptionWrapper;
import de.retest.recheck.ui.actions.TargetNotFoundException;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.PathAttribute;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.AttributesDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.RootElementDifference;
import de.retest.recheck.util.ApprovalsUtil;

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
		when( result.getAllElementDifferences() ).thenReturn( Collections.singletonList( rootDifference ) );

		final String string = cut.toString( result );

		assertThat( string ).startsWith( "foo resulted in:\n" );
	}

	@Test
	void toString_should_respect_indent() {
		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getAllElementDifferences() ).thenReturn( Collections.emptyList() );

		final String string = cut.toString( result, "____" );

		assertThat( string ).startsWith( "____" );
	}

	@Test
	void toString_should_print_message_if_no_recheck_action_replay_result() {
		final RootElement element = mock( RootElement.class );
		when( element.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		final SutState state = mock( SutState.class );
		when( state.getRootElements() ).thenReturn( Collections.singletonList( element ) );

		final String goldenMasterPath = "some/golden/master/path";

		final NoGoldenMasterActionReplayResult result =
				new NoGoldenMasterActionReplayResult( "foo", state, goldenMasterPath );

		final String string = cut.toString( result );

		assertThat( string ).contains( NoGoldenMasterActionReplayResult.MSG_LONG );
	}

	@Test
	void toString_should_not_print_differences_if_no_state() {
		final ActionReplayResult replayResult = mock( ActionReplayResult.class );
		when( replayResult.getDescription() ).thenReturn( "foo" );
		when( replayResult.hasDifferences() ).thenCallRealMethod();
		when( replayResult.getStateDifference() ).thenReturn( null );

		assertThat( cut.toString( replayResult ) ).isEqualTo( "foo resulted in:\n\tno differences" );
	}

	@Test
	void toString_should_respect_indent_if_no_state() {
		final ActionReplayResult replayResult = mock( ActionReplayResult.class );
		when( replayResult.getDescription() ).thenReturn( "foo" );
		when( replayResult.hasDifferences() ).thenCallRealMethod();
		when( replayResult.getStateDifference() ).thenReturn( null );

		assertThat( cut.toString( replayResult, "____" ) ).isEqualTo( "____foo resulted in:\n____\tno differences" );
	}

	@Test
	void toString_should_not_print_child_differences_if_insertion_or_deletion() {
		final List<ElementDifference> empty = Collections.emptyList();

		final ElementDifference differences = change( Path.fromString( "html[1]/body[1]" ), Arrays.asList( //
				delete( Path.fromString( "html[1]/body[1]/div[1]" ), Arrays.asList( // 
						delete( Path.fromString( "html[1]/body[1]/div[1]/div[1]" ), Arrays.asList( // 
								delete( Path.fromString( "html[1]/body[1]/div[1]/div[1]/div[1]" ), empty ), // 
								delete( Path.fromString( "html[1]/body[1]/div[1]/div[1]/div[2]" ), empty ) //
						) ), //
						delete( Path.fromString( "html[1]/body[1]/div[1]/div[2]" ), empty ) //
				) ), //
				change( Path.fromString( "html[1]/body[1]/div[2]" ), Arrays.asList( // 
						delete( Path.fromString( "html[1]/body[1]/div[2]/div[1]" ), Arrays.asList( // 
								delete( Path.fromString( "html[1]/body[1]/div[2]/div[1]/div[1]" ), empty ), //
								delete( Path.fromString( "html[1]/body[1]/div[2]/div[1]/div[2]" ), empty ) //
						) ), //
						change( Path.fromString( "html[1]/body[1]/div[2]/div[2]" ), empty ) ) //
				) ) );

		final RootElementDifference rootDifference =
				new RootElementDifference( differences, mock( RootElement.class ), mock( RootElement.class ) );

		final ActionReplayResult replayResult = ActionReplayResult.withDifference( ActionReplayData.ofSutStart(),
				WindowRetriever.empty(), DifferenceRetriever.of( Collections.singletonList( rootDifference ) ), 0L );

		ApprovalsUtil.verify( cut.toString( replayResult ) );
	}

	private ElementDifference delete( final Path path, final List<ElementDifference> childDifferences ) {
		final Element element = element( path );

		final InsertedDeletedElementDifference insertion =
				InsertedDeletedElementDifference.differenceFor( element, null );

		return new ElementDifference( element, null, insertion, null, null, childDifferences );
	}

	private ElementDifference change( final Path path, final List<ElementDifference> childDifferences ) {
		final Element element = element( path );

		final AttributesDifference attributes = new AttributesDifference( Arrays.asList( // 
				new AttributeDifference( "foo-1", "bar-1", "bar1" ), //
				new AttributeDifference( "foo-2", "bar-2", "bar2" ), //
				new AttributeDifference( "foo-3", "bar-3", "bar3" ) //
		) );

		return new ElementDifference( element, attributes, null, null, null, childDifferences );
	}

	private Element element( final Path path ) {
		final PathAttribute pathAttribute = new PathAttribute( path );

		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.getPath() ).thenReturn( path.toString() );
		when( identifyingAttributes.toString() ).thenReturn( path.getElement().toString() );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		return element;
	}
}
