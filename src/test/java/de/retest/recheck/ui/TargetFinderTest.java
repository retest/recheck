package de.retest.recheck.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.retest.recheck.ui.actions.AbstractAction;
import de.retest.recheck.ui.actions.TargetNotFoundException;
import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.components.RootContainer;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

@SuppressWarnings( "unchecked" )
@RunWith( PowerMockRunner.class )
@PrepareForTest( { TargetFinder.class } )
public class TargetFinderTest {

	private final Attributes attributes = new Attributes();
	private final IdentifyingAttributes actionIdentAttributes = IdentifyingAttributes.create(
			Path.path( Path.path( new PathElement( "root", 1 ) ), new PathElement( "TargetFinderTest$Comp", 1 ) ),
			Comp.class );
	private final IdentifyingAttributes otherIdentAttributes =
			IdentifyingAttributes.create( Path.path( Path.path( new PathElement( "root[1]/other", 1 ) ),
					new PathElement( "TargetFinderTest$Comp", 1 ) ), Comp.class );

	private final Element element = Element.create( "id", mock( Element.class ), actionIdentAttributes, attributes );

	private final AbstractAction action = mock( AbstractAction.class );

	private final Component<java.awt.Component> component = mock( Component.class );
	private final RootContainer<java.awt.Component> window = mock( RootContainer.class );

	private final org.slf4j.Logger targetFinderLogger = mock( org.slf4j.Logger.class );

	@Before
	public void setUp() throws Exception {
		final Field field = MemberMatcher.field( TargetFinder.class, "logger" );
		field.set( TargetFinder.class, targetFinderLogger );

		when( action.getTargetElement() ).thenReturn( element );
		when( window.getBestMatch( any( IdentifyingAttributes.class ) ) ).thenReturn( component );
		when( window.getTextWithComponents() ).thenReturn( "text with components" );
	}

	@Test
	public void no_targetable_windows() throws Exception {
		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, new ArrayList<>(), null );

		assertThat( result.left.toString() ).isEqualTo(
				"de.retest.recheck.ui.actions.TargetNotFoundException: Action action: Could not resolve component root[1]/TargetFinderTest$Comp[1]! No best match found!" );

		verify( targetFinderLogger ).error( "Could not find component {} in {} window(s): ",
				"root[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1", 0 );
		verifyNoMoreInteractions( targetFinderLogger );
	}

	@Test
	public void targetable_window_but_no_best_match() throws Exception {
		// window returns no best match:
		when( window.getBestMatch( any( IdentifyingAttributes.class ) ) ).thenReturn( null );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		assertThat( result.left.toString() ).isEqualTo(
				"de.retest.recheck.ui.actions.TargetNotFoundException: Action action: Could not resolve component root[1]/TargetFinderTest$Comp[1]! No best match found!" );

		verify( targetFinderLogger ).error( "Could not find component {} in {} window(s): ",
				"root[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1", 1 );
		verify( targetFinderLogger ).error( "Window no. {}: {}", 1, "text with components" );
		verifyNoMoreInteractions( targetFinderLogger );
	}

	@Test
	public void same_identifyingAttributes_in_action_and_best_match_of_single_window() throws Exception {
		when( component.getIdentifyingAttributes() ).thenReturn( actionIdentAttributes );
		when( component.match( any( IdentifyingAttributes.class ) ) )
				.thenReturn( actionIdentAttributes.match( actionIdentAttributes ) );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		assertThat( result.left ).isNull();
		assertThat( result.right ).isSameAs( component );

		verifyZeroInteractions( targetFinderLogger );
	}

	@Test
	public void other_identifyingAttributes_in_best_match_and_match_is_0() throws Exception {
		when( component.getIdentifyingAttributes() ).thenReturn( otherIdentAttributes );
		when( component.match( any( IdentifyingAttributes.class ) ) ).thenReturn( 0.0d );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		assertThat( result.left ).isNotNull();
		assertThat( result.left.toString() ).isEqualTo(
				"de.retest.recheck.ui.actions.TargetNotFoundException: Action action: Could not resolve component root[1]/TargetFinderTest$Comp[1]! No best match found!" );

		verify( targetFinderLogger ).error( "Could not find component {} in {} window(s): ",
				"root[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1", 1 );
		verify( targetFinderLogger ).error( "Window no. {}: {}", 1, "text with components" );
		verifyNoMoreInteractions( targetFinderLogger );
	}

	@Test
	public void other_identifyingAttributes_in_best_match_and_match_is_less_than_threshold() throws Exception {
		final double match = TargetFinder.ELEMENT_MATCH_THRESHOLD - 0.01;
		when( component.match( any( IdentifyingAttributes.class ) ) ).thenReturn( match );

		final IdentifyingAttributes otherIdentAttributes = mock( IdentifyingAttributes.class );
		when( otherIdentAttributes.get( eq( "text" ) ) ).thenReturn( "some text that doesn't match" );
		when( otherIdentAttributes.getType() ).thenReturn( this.otherIdentAttributes.getType() );
		when( otherIdentAttributes.toFullString() ).thenReturn( this.otherIdentAttributes.toFullString() );
		when( component.getIdentifyingAttributes() ).thenReturn( otherIdentAttributes );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		final String percentageOfBestMatch = String.format( "%.2f", match * 100.0 );
		assertThat( result.left.toString() ).isEqualTo(
				"de.retest.recheck.ui.actions.TargetNotFoundException: Action action: Could not find component root[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1 in 1 windows! Best with "
						+ percentageOfBestMatch
						+ "% match is root[1]/other[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1" );

		verify( targetFinderLogger ).error( "Could not find component {} in {} window(s): ",
				"root[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1", 1 );
		verify( targetFinderLogger ).error( "Window no. {}: {}", 1, "text with components" );
		verify( targetFinderLogger ).error( "Best with {}% match is {}", percentageOfBestMatch,
				"root[1]/other[1] # de.retest.recheck.ui.TargetFinderTest$Comp # 1" );
		verifyNoMoreInteractions( targetFinderLogger );
	}

	@Test
	public void other_identifyingAttributes_in_best_match_and_match_is_equal_to_threshold() throws Exception {
		final double match = TargetFinder.ELEMENT_MATCH_THRESHOLD;

		when( component.getIdentifyingAttributes() ).thenReturn( otherIdentAttributes );
		when( component.match( any( IdentifyingAttributes.class ) ) ).thenReturn( match );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		assertThat( result.left ).isNull();
		assertThat( result.right ).isSameAs( component );

		verifyNoMoreInteractions( targetFinderLogger );
	}

	@Test
	public void other_identifyingAttributes_in_best_match_and_match_is_greater_than_threshold() throws Exception {
		final double match = TargetFinder.ELEMENT_MATCH_THRESHOLD + 0.01d;

		when( component.getIdentifyingAttributes() ).thenReturn( otherIdentAttributes );
		when( component.match( any( IdentifyingAttributes.class ) ) ).thenReturn( match );

		final ImmutablePair<TargetNotFoundException, Component<java.awt.Component>> result =
				TargetFinder.findTargetComponent( action, Collections.singletonList( window ), null );

		assertThat( result.left ).isNull();
		assertThat( result.right ).isSameAs( component );

	}

	private static class Comp {}
}
