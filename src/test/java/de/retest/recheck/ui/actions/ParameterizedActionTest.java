package de.retest.recheck.ui.actions;

import static de.retest.recheck.ui.descriptors.StringAttribute.parameterTypeInteger;
import static de.retest.recheck.ui.descriptors.StringAttribute.parameterTypeString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.MutableAttributes;
import de.retest.recheck.ui.descriptors.ParameterType;
import de.retest.recheck.ui.image.Screenshot;

public class ParameterizedActionTest {

	@BeforeClass
	public static void setUpOnce() throws Exception {
		ParameterType.registerStdParameterTypes();
	}

	@Test
	public void ActionParameters_can_be_iterated_over() {
		final ParameterizedAction action = buildParameterizedAction();
		final ParameterizedAction derived = new ParameterizedAction( action );
		derived.getParameter( "something" );
		// in case of doubly wrapped unmodifiable collections, this causes StackOverflowError
	}

	@Test
	public void applyChanges_should_replace_existing_correctly() {
		final ParameterizedAction action = buildParameterizedAction();
		final ParameterizedAction changes = action.applyChanges( text( "6" ), row( "2" ) );

		assertThat( action ).isNotEqualTo( changes );
		assertThat( (String) changes.getParameterValue( "text" ) ).isEqualTo( "6" );
		assertThat( (Integer) changes.getParameterValue( "row" ) ).isEqualTo( 2 );
	}

	@Test
	public void applyChanges_should_keep_non_changed_parameters() {
		final ParameterizedAction action = buildParameterizedAction();
		final ParameterizedAction changes = action.applyChanges( text( "6" ) );

		assertThat( action ).isNotEqualTo( changes );
		assertThat( (String) changes.getParameterValue( "text" ) ).isEqualTo( "6" );
		assertThat( (Integer) changes.getParameterValue( "row" ) ).isEqualTo( 5 );
	}

	@Test
	public void applyChanges_should_not_add_new_parameters() {
		final ParameterizedAction action = buildParameterizedAction();
		final ParameterizedAction changes =
				action.applyChanges( new ActionParameter( "new", "5", parameterTypeString ) );

		assertThat( action ).isEqualTo( changes );
	}

	public static ParameterizedAction buildParameterizedAction() {
		final IdentifyingAttributes identAttributes =
				IdentifyingAttributes.create( Path.path( new PathElement( "path", 0 ) ), SomeType.class );
		final MutableAttributes state = new MutableAttributes();
		state.put( "value", "SomeValue" );
		final Element element = Element.create( "id", mock( Element.class ), identAttributes, state.immutable() );
		final Screenshot[] windows = null;
		return new ParameterizedAction( element, windows, SomeAction.class, "desc", text( "string" ), row( "5" ) );
	}

	private static ActionParameter row( final String value ) {
		return new ActionParameter( "row", value, parameterTypeInteger );
	}

	private static ActionParameter text( final String value ) {
		return new ActionParameter( "text", value, parameterTypeString );
	}

	static class SomeType {}

	static class SomeAction {}

}
