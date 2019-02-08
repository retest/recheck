package de.retest.recheck.ui.actions;

import static de.retest.recheck.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.actions.AbstractAction;
import de.retest.recheck.ui.actions.Action;
import de.retest.recheck.ui.actions.ActionExecutionResult;
import de.retest.recheck.ui.actions.TargetNotFoundException;
import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.review.ActionChangeSet;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { AbstractAction.class } )
public class AbstractActionImpl_execute_Test {

	@SuppressWarnings( "unchecked" )
	private final Environment<java.awt.Component> environment = Mockito.mock( Environment.class );

	private final IdentifyingAttributes actionIdentAttributes =
			IdentifyingAttributes.create( fromString( "Window[0]" ), Comp.class );
	private final Element element = Mockito.mock( Element.class );

	@SuppressWarnings( "unchecked" )
	private final Component<java.awt.Component> targetComponent = Mockito.mock( Component.class );

	private final org.slf4j.Logger actionLogger = Mockito.mock( org.slf4j.Logger.class );

	@Before
	public void setUp() throws Exception {
		when( element.getIdentifyingAttributes() ).thenReturn( actionIdentAttributes );

		final Field field = MemberMatcher.field( AbstractAction.class, "logger" );
		field.set( AbstractAction.class, actionLogger );
	}

	@Test
	public void environment_does_not_find_target() throws Exception {
		final TargetNotFoundException targetNotFound =
				new TargetNotFoundException( null, null, null, "Hmmm... Where did it go?" );

		when( environment.findTargetComponent( any( AbstractAction.class ) ) )
				.thenReturn( new ImmutablePair<>( targetNotFound, null ) );

		final ActionExecutionResult result = action( element ).execute( environment );

		assertThat( result.getDuration() ).isEqualTo( -1 );
		assertThat( result.getTargetNotFound() ).isEqualTo( targetNotFound );

		verifyZeroInteractions( actionLogger );
	}

	@Test
	public void environment_finds_target() throws Exception {

		when( environment.findTargetComponent( any( AbstractAction.class ) ) )
				.thenReturn( new ImmutablePair<>( null, targetComponent ) );

		final ActionExecutionResult result = action( element ).execute( environment );

		assertThat( result.getDuration() ).isEqualTo( -1 );
		assertThat( result.getError() ).isNull();
		assertThat( result.getTargetNotFound() ).isNull();

		verifyZeroInteractions( actionLogger );
	}

	@Test
	public void environment_finds_target_and_exception_happens_later() throws Exception {

		when( environment.findTargetComponent( any( AbstractAction.class ) ) )
				.thenReturn( new ImmutablePair<>( null, targetComponent ) );
		final Element throwingDescriptor = Mockito.mock( Element.class );
		when( throwingDescriptor.getScreenshot() ).thenThrow( new NullPointerException() );

		final ActionExecutionResult result = action( throwingDescriptor ).execute( environment );

		assertThat( result.getDuration() ).isEqualTo( -1 );
		assertThat( result.getError().getThrowable() ).isInstanceOf( NullPointerException.class );
		assertThat( result.getTargetNotFound() ).isNull();

		verifyZeroInteractions( actionLogger );
	}

	private AbstractAction action( final Element descriptor ) {
		return new AbstractAction( descriptor, new Screenshot[] {} ) {

			private static final long serialVersionUID = 1L;

			@Override
			public void execute( final Component<?> component ) {}

			@Override
			public String createDescription() {
				return null;
			}

			@Override
			public Action applyChanges( final ActionChangeSet componentChanges ) {
				return null;
			}
		};
	}

	private static class Comp {}
}
