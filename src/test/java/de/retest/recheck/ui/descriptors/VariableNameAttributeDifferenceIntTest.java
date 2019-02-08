package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.DefaultAttribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.OutlineAttribute;
import de.retest.recheck.ui.descriptors.ParameterizedAttribute;
import de.retest.recheck.ui.descriptors.StringAttribute;
import de.retest.recheck.ui.descriptors.VariableNameAttributeDifference;
import de.retest.recheck.ui.diff.AttributeDifference;

public class VariableNameAttributeDifferenceIntTest {

	OutlineAttribute outlineAttribute;
	DefaultAttribute defaultAttribute;
	StringAttribute stringAttribute;
	IdentifyingAttributes attributes;

	@Before
	public void setUp() throws Exception {
		outlineAttribute = OutlineAttribute.create( new Rectangle( 0, 0, 0, 0 ) );
		defaultAttribute = new DefaultAttribute( "key1", "value" );
		stringAttribute = new StringAttribute( "key2", "value" );

		attributes = new IdentifyingAttributes( Arrays.asList( outlineAttribute, defaultAttribute, stringAttribute ) );
	}

	@Test
	public void applyChangeTo_should_change_variable_name_of_parameterized_attributes() {
		final Set<AttributeDifference> changes = new HashSet<>();
		changes.add( new VariableNameAttributeDifference( defaultAttribute, "variable1" ) );
		changes.add( new VariableNameAttributeDifference( stringAttribute, "variable2" ) );

		final IdentifyingAttributes changed = attributes.applyChanges( changes );

		final Attribute defaultAttr = changed.getAttribute( defaultAttribute.getKey() );
		assertThat( defaultAttr ).isInstanceOf( ParameterizedAttribute.class );
		assertThat( ((ParameterizedAttribute) defaultAttr).getVariableName() ).isEqualTo( "variable1" );
		final Attribute stringAttr = changed.getAttribute( stringAttribute.getKey() );
		assertThat( stringAttr ).isInstanceOf( ParameterizedAttribute.class );
		assertThat( ((ParameterizedAttribute) stringAttr).getVariableName() ).isEqualTo( "variable2" );
	}
}
