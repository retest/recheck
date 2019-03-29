package de.retest.recheck.report;

import static de.retest.recheck.ignore.ShouldIgnore.IGNORE_NOTHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.retest.recheck.execution.RecheckDifferenceFinder;
import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.AttributeShouldIgnore;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.descriptors.SutState;

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

		final AttributeShouldIgnore shouldIgnore = new AttributeShouldIgnore( "path" );

		assertThat( differences.hasDifferences() ).isTrue();
		assertThat( differences.getDifferences( IGNORE_NOTHING ) ).hasSize( 1 );
		assertThat( differences.getDifferences( shouldIgnore ) ).hasSize( 0 );
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
		final AttributeShouldIgnore shouldIgnoreFont = new AttributeShouldIgnore( "font" );
		final AttributeShouldIgnore shouldIgnoreForeground = new AttributeShouldIgnore( "foreground" );
		final AttributeShouldIgnore shouldIgnorePath = new AttributeShouldIgnore( "path" );
		final AttributeShouldIgnore shouldIgnoreBackground = new AttributeShouldIgnore( "background" );

		final ShouldIgnore shouldIgnoreAll = mock( ShouldIgnore.class );
		when( shouldIgnoreAll.shouldIgnoreElement( expectedElement ) ).thenReturn( true );

		assertThat( differences.hasDifferences() ).isTrue();

		assertThat( differences.getDifferences( IGNORE_NOTHING ) ).hasSize( 4 );
		assertThat( differences.getDifferences( shouldIgnoreAll ) ).hasSize( 0 );

		assertThat( differences.getDifferences( shouldIgnoreFont ) ).hasSize( 3 );
		assertThat( differences.getDifferences( shouldIgnoreForeground ) ).hasSize( 3 );
		assertThat( differences.getDifferences( shouldIgnorePath ) ).hasSize( 3 );
		assertThat( differences.getDifferences( shouldIgnoreBackground ) ).hasSize( 3 );

		assertThat( differences.getStateDifference() ).isNotNull();
		assertThat( differences.getWindows() ).isEmpty();
	}

}
