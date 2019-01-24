package de.retest.ui.actions;

import static de.retest.ui.descriptors.StringAttribute.parameterTypeString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class ParameterizedActionConverterTest {

	@Test
	public void roundTrip_conversion_should_be_lossless() {
		final ParameterizedAction action = ParameterizedActionTest.buildParameterizedAction();

		final ParameterizedActionConverter converter = new ParameterizedActionConverter( "action" );
		final Collection<ActionParameter> converted = Arrays.asList( converter.toActionParameter( action ) );
		final ParameterizedAction reconstructed = converter.toAction( converted );

		assertThat( converted ).hasSize( 7 );
		assertThat( reconstructed ).isEqualTo( action );
	}

	@Test
	public void conversion_should_consider_seperator() throws Exception {
		final ParameterizedAction action = ParameterizedActionTest.buildParameterizedAction();

		final ParameterizedActionConverter converter = new ParameterizedActionConverter( "action" );
		final Collection<ActionParameter> converted = new ArrayList<>();
		converted.addAll( Arrays.asList( converter.toActionParameter( action ) ) );
		converted.add( new ActionParameter( "action_thisShouldNeverExistAsAParameterInSubaction", "foo",
				parameterTypeString ) );
		final ParameterizedAction reconstructed = converter.toAction( converted );

		assertThat( converted ).hasSize( 8 );
		assertThat( reconstructed ).isEqualTo( action );
	}

}
