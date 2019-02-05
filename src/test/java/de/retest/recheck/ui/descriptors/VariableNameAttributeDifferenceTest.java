package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.DefaultAttribute;
import de.retest.recheck.ui.descriptors.ParameterizedAttribute;
import de.retest.recheck.ui.descriptors.VariableNameAttributeDifference;

public class VariableNameAttributeDifferenceTest {

	@Test
	public void applyChangeTo_should_change_variable() {
		final ParameterizedAttribute attribute = new DefaultAttribute( "key", "value" );
		final VariableNameAttributeDifference difference = new VariableNameAttributeDifference( attribute, "variable" );

		final Attribute changed = difference.applyChangeTo( attribute );

		assertThat( attribute.getVariableName() ).isEqualTo( null );
		assertThat( changed ).isInstanceOf( ParameterizedAttribute.class );
		assertThat( ((ParameterizedAttribute) changed).getVariableName() ).isEqualTo( "variable" );
	}
}
