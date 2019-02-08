package de.retest.recheck.ui.descriptors;

import static de.retest.recheck.ui.descriptors.StringAttribute.parameterTypeClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import org.junit.Test;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.ParameterType;
import de.retest.recheck.ui.descriptors.ParameterizedAttribute;
import de.retest.recheck.ui.descriptors.ParseStringAttributeDifference;

public class ParseStringAttributeDifferenceTest {

	@Test
	public void applyChangeTo_should_correctly_change_value_based_on_type() {
		final ClassAttribute attribute = new ClassAttribute( getClass(), "none" );

		final ParseStringAttributeDifference difference =
				new ParseStringAttributeDifference( attribute, Integer.class.getName() );

		final Attribute change = difference.applyChangeTo( attribute );

		assertThat( change ).isInstanceOf( ParameterizedAttribute.class );
		assertThat( change.getValue() ).isEqualTo( Integer.class );
	}

	@Test
	public void apply_change_should_return_old_attribute_on_error() {
		final ClassAttribute attribute = new ClassAttribute( getClass(), "none" );

		final ParseStringAttributeDifference difference = new ParseStringAttributeDifference( attribute, "42" );

		final Attribute change = difference.applyChangeTo( attribute );

		assertThat( change ).isEqualTo( attribute );
	}

	private static class ClassAttribute extends ParameterizedAttribute {

		private static final long serialVersionUID = 1L;

		private final Class<?> clazz;

		public ClassAttribute( final Class<?> clazz, final String variableName ) {
			super( "class", variableName );
			this.clazz = clazz;
		}

		@Override
		public ParameterizedAttribute applyVariableChange( final String variableName ) {
			return new ClassAttribute( clazz, variableName );
		}

		@Override
		public ParameterType getType() {
			return parameterTypeClass;
		}

		@Override
		public Serializable getValue() {
			return clazz;
		}

		@Override
		public double match( final Attribute other ) {
			return 0;
		}

		@Override
		public Attribute applyChanges( final Serializable actual ) {
			return new ClassAttribute( (Class<?>) actual, getVariableName() );
		}
	}
}
