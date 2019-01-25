package de.retest.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import de.retest.ui.Path;

@RunWith( Theories.class )
public class AttributeImplTest {

	@DataPoints
	public static final Attribute[] attributeImplementations = new Attribute[] { //
			OutlineAttribute.create( new Rectangle( 10, 10, 40, 20 ) ), //
			new StringAttribute( "name", "my name" ), //
			new ContextAttribute( "some context" ), //
			new SuffixAttribute( 0 ), //
			new WeightedTextAttribute( "text", "weighted text" ), //
			new TextAttribute( "text", "some text" ), //
			new CodeLocAttribute( "[some.Method:38]" ), //
			new PathAttribute( Path.fromString( "Window/Panel[0]" ) ), //
			new DefaultAttribute( "key", "value" ), //
	};

	// for testing Attribute.applyChange( value )
	public static Serializable[] changedValues = new Serializable[] { //
			new Rectangle( 10, 10, 50, 50 ), //,
			"new name", //
			"other context", //
			"0", //
			"weighted_new_text", //
			"other text", //
			"[other.Method:42]", //
			Path.fromString( "Window/Panel[0]" ), //
			"other value", //
	};

	public static Map<Attribute, Serializable> valueChange;

	@BeforeClass
	public static void setUpOnce() throws Exception {
		valueChange = new HashMap<>();

		for ( int i = 0; i < attributeImplementations.length; i++ ) {
			valueChange.put( attributeImplementations[i], changedValues[i] );
		}
	}

	@Theory
	public void attribute_weight_should_be_valid( final Attribute attribute ) throws Exception {
		assertThat( attribute.getWeight() ).isBetween( 0.0, 5.0 );
	}

	@Theory
	public void attribute_should_match_equal_and_compare_to_itself( final Attribute attribute ) throws Exception {
		assertThat( attribute.equals( attribute ) ).isTrue();
		assertThat( attribute.match( attribute ) ).isEqualTo( Attribute.FULL_MATCH );
		assertThat( attribute.compareTo( attribute ) ).isEqualTo( Attribute.COMPARE_EQUAL );
	}

	@Theory
	public void methods_should_be_null_save( final Attribute attribute ) throws Exception {
		assertThat( attribute.equals( null ) ).isFalse();
		assertThat( attribute.match( null ) ).isEqualTo( Attribute.NO_MATCH );
		assertThat( attribute.compareTo( null ) ).isEqualTo( Attribute.COMPARE_BIGGER );
	}

	@Theory
	public void applyChange_should_return_same_attribute_type( final Attribute attribute ) throws Exception {
		final Serializable value = valueChange.get( attribute );
		final Attribute changed = attribute.applyChanges( value );

		assertThat( changed ).hasSameClassAs( attribute );
		assertThat( changed.getValue() ).isEqualTo( value );
	}

	@Theory
	public void applyChange_should_not_change_variable_name( final Attribute attribute ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final ParameterizedAttribute param = (ParameterizedAttribute) attribute;

			final ParameterizedAttribute changed1 = param.applyVariableChange( "new-variable" );
			final ParameterizedAttribute changed2 =
					(ParameterizedAttribute) changed1.applyChanges( valueChange.get( attribute ) );

			assertThat( changed2.getVariableName() ).isEqualTo( changed1.getVariableName() );
		}
	}

	@Theory
	public void applyVariableChange_should_return_same_attribute_type( final Attribute attribute ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final ParameterizedAttribute param = (ParameterizedAttribute) attribute;

			final ParameterizedAttribute changed1 = param.applyVariableChange( "new-variable" );

			assertThat( changed1 ).hasSameClassAs( attribute );
			assertThat( changed1.getVariableName() ).isEqualTo( "new-variable" );
			assertThat( changed1.getValue() ).isEqualTo( param.getValue() );

			final ParameterizedAttribute changed2 =
					(ParameterizedAttribute) changed1.applyChanges( valueChange.get( attribute ) );

			assertThat( changed2.getVariableName() ).isEqualTo( changed1.getVariableName() );
		}
	}

	@Theory
	public void applyVariableChange_should_not_change_value( final Attribute attribute ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final ParameterizedAttribute param = (ParameterizedAttribute) attribute;

			final ParameterizedAttribute changed1 = param.applyVariableChange( "new-variable" );

			assertThat( changed1.getValue() ).isEqualTo( param.getValue() );
		}
	}
}
