package de.retest.recheck.ui.descriptors;

import de.retest.recheck.ui.diff.AttributeDifference;

public class VariableNameAttributeDifference extends AttributeDifference {

	private static final long serialVersionUID = 1L;

	public VariableNameAttributeDifference( final ParameterizedAttribute attribute, final String variableName ) {
		super( attribute.getKey(), attribute.getVariableName(), variableName );
	}

	@Override
	public Attribute applyChangeTo( final Attribute attribute ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final ParameterizedAttribute parameterizedAttribute = (ParameterizedAttribute) attribute;
			warnIfAttributesDontMatch( parameterizedAttribute.getVariableName() );
			return parameterizedAttribute.applyVariableChange( (String) getActual() );
		}
		return attribute;
	}
}
