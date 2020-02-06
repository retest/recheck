package de.retest.recheck.printer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.NoGoldenMasterActionReplayResult;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.action.ActionReplayData;
import de.retest.recheck.report.action.DifferenceRetriever;
import de.retest.recheck.report.action.WindowRetriever;
import de.retest.recheck.ui.Path;
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
import de.retest.recheck.ui.diff.StateDifference;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;
import de.retest.recheck.util.ApprovalsUtil;

class ActionReplayResultPrinterTest {

	ActionReplayResultPrinter cut;

	@BeforeEach
	void setUp() {
		cut = new ActionReplayResultPrinter( ( identifyingAttributes, attributeKey, attributeValue ) -> false );
	}

	@Test
	void toString_should_print_state_differences_if_no_meta_differences() {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "Identifying" );
		when( identifyingAttributes.getPath() ).thenReturn( "path/to/element" );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "key" );
		when( attributeDifference.getActual() ).thenReturn( "actual" );
		when( attributeDifference.getExpected() ).thenReturn( "expected" );

		final ElementDifference rootDifference = mock( ElementDifference.class );
		when( rootDifference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( rootDifference.hasAnyDifference() ).thenReturn( true );
		when( rootDifference.getAttributeDifferences() ).thenReturn( Collections.singletonList( attributeDifference ) );

		final RootElementDifference root = mock( RootElementDifference.class );
		when( root.getElementDifference() ).thenReturn( rootDifference );

		final StateDifference stateDifference = mock( StateDifference.class );
		when( stateDifference.getRootElementDifferences() ).thenReturn( Collections.singletonList( root ) );

		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getDescription() ).thenReturn( "foo" );
		when( result.hasDifferences() ).thenReturn( true );
		when( result.getStateDifference() ).thenReturn( stateDifference );
		when( result.getMetadataDifference() ).thenReturn( MetadataDifference.empty() );

		final String string = cut.toString( result );

		assertThat( string ).isEqualTo( "foo resulted in:\n" //
				+ "\tIdentifying at 'path/to/element':\n" //
				+ "\t\tkey: expected=\"expected\", actual=\"actual\"" );
	}

	@Test
	void toString_should_print_meta_differences_if_no_state_differences() throws Exception {
		final StateDifference stateDifference = mock( StateDifference.class );
		when( stateDifference.getRootElementDifferences() ).thenReturn( Collections.emptyList() );

		final MetadataDifference metadataDifference =
				MetadataDifference.of( new HashSet<>( Arrays.asList( new MetadataElementDifference( "a", "b", "c" ), //
						new MetadataElementDifference( "b", "c", "d" ) //
				) ) );

		final ActionReplayResult actionResult = mock( ActionReplayResult.class );
		when( actionResult.getDescription() ).thenReturn( "foo" );
		when( actionResult.getStateDifference() ).thenReturn( stateDifference );
		when( actionResult.getMetadataDifference() ).thenReturn( metadataDifference );

		assertThat( cut.toString( actionResult ) ).isEqualTo( "foo resulted in:\n" //
				+ "\tMetadata Differences:\n" //
				+ "\t  Please note that these differences do not affect the result and are not included in the difference count.\n" //
				+ "\t\ta: expected=\"b\", actual=\"c\"\n" //
				+ "\t\tb: expected=\"c\", actual=\"d\"" );
	}

	@Test
	void toString_should_print_meta_before_state_differences() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.toString() ).thenReturn( "Identifying" );
		when( identifyingAttributes.getPath() ).thenReturn( "path/to/element" );

		final AttributeDifference attributeDifference = mock( AttributeDifference.class );
		when( attributeDifference.getKey() ).thenReturn( "key" );
		when( attributeDifference.getActual() ).thenReturn( "actual" );
		when( attributeDifference.getExpected() ).thenReturn( "expected" );

		final ElementDifference rootDifference = mock( ElementDifference.class );
		when( rootDifference.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		when( rootDifference.hasAnyDifference() ).thenReturn( true );
		when( rootDifference.getAttributeDifferences() ).thenReturn( Collections.singletonList( attributeDifference ) );

		final RootElementDifference root = mock( RootElementDifference.class );
		when( root.getElementDifference() ).thenReturn( rootDifference );

		final StateDifference stateDifference = mock( StateDifference.class );
		when( stateDifference.getRootElementDifferences() ).thenReturn( Collections.singletonList( root ) );

		final MetadataDifference metadataDifference =
				MetadataDifference.of( new HashSet<>( Arrays.asList( new MetadataElementDifference( "a", "b", "c" ), //
						new MetadataElementDifference( "b", "c", "d" ) //
				) ) );

		final ActionReplayResult actionResult = mock( ActionReplayResult.class );
		when( actionResult.getDescription() ).thenReturn( "foo" );
		when( actionResult.getStateDifference() ).thenReturn( stateDifference );
		when( actionResult.hasDifferences() ).thenReturn( true );
		when( actionResult.getMetadataDifference() ).thenReturn( metadataDifference );

		assertThat( cut.toString( actionResult ) ).isEqualTo( "foo resulted in:\n" //
				+ "\tMetadata Differences:\n" //
				+ "\t  Please note that these differences do not affect the result and are not included in the difference count.\n" //
				+ "\t\ta: expected=\"b\", actual=\"c\"\n" //
				+ "\t\tb: expected=\"c\", actual=\"d\"\n" //
				+ "\tIdentifying at 'path/to/element':\n" //
				+ "\t\tkey: expected=\"expected\", actual=\"actual\"" );
	}

	@Test
	void toString_should_respect_indent() {
		final ActionReplayResult result = mock( ActionReplayResult.class );
		when( result.getStateDifference() ).thenReturn( mock( StateDifference.class ) );
		when( result.getMetadataDifference() ).thenReturn( MetadataDifference.empty() );

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
	void toString_should_not_print_child_differences_if_insertion_or_deletion() {
		final List<ElementDifference> empty = Collections.emptyList();

		final ElementDifference differences = change( "html[1]/body[1]", //
				delete( "html[1]/body[1]/div[1]", //
						delete( "html[1]/body[1]/div[1]/div[1]", //
								delete( "html[1]/body[1]/div[1]/div[1]/div[1]" ), //
								delete( "html[1]/body[1]/div[1]/div[1]/div[2]" ) //
						), //
						delete( "html[1]/body[1]/div[1]/div[2]" ) //
				), //
				change( "html[1]/body[1]/div[2]", //
						delete( "html[1]/body[1]/div[2]/div[1]", //
								delete( "html[1]/body[1]/div[2]/div[1]/div[1]" ), //
								delete( "html[1]/body[1]/div[2]/div[1]/div[2]" ) //
						) ), //
				change( "html[1]/body[1]/div[2]/div[2]" ) //
		);

		final RootElementDifference rootDifference =
				new RootElementDifference( differences, mock( RootElement.class ), mock( RootElement.class ) );

		final ActionReplayResult replayResult = ActionReplayResult.withDifference( ActionReplayData.ofSutStart(),
				WindowRetriever.empty(),
				DifferenceRetriever.of( new StateDifference( Collections.singletonList( rootDifference ) ) ), 0L );

		ApprovalsUtil.verify( cut.toString( replayResult ) );
	}

	private ElementDifference delete( final String path, final ElementDifference... childDifferences ) {
		final Element element = element( path );

		final InsertedDeletedElementDifference insertion =
				InsertedDeletedElementDifference.differenceFor( element, null );

		return new ElementDifference( element, null, insertion, null, null, Arrays.asList( childDifferences ) );
	}

	private ElementDifference change( final String path, final ElementDifference... childDifferences ) {
		final Element element = element( path );

		final AttributesDifference attributes = new AttributesDifference( Arrays.asList( //
				new AttributeDifference( "foo-1", "bar-1", "bar1" ), //
				new AttributeDifference( "foo-2", "bar-2", "bar2" ), //
				new AttributeDifference( "foo-3", "bar-3", "bar3" ) //
		) );

		return new ElementDifference( element, attributes, null, null, null, Arrays.asList( childDifferences ) );
	}

	private Element element( final String pathAsString ) {
		final Path path = Path.fromString( pathAsString );
		final PathAttribute pathAttribute = new PathAttribute( path );

		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.getPath() ).thenReturn( path.toString() );
		when( identifyingAttributes.toString() ).thenReturn( path.getElement().toString() );

		final Element element = mock( Element.class );
		when( element.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
		return element;
	}
}
