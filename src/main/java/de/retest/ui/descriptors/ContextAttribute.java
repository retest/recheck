package de.retest.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContextAttribute extends StringAttribute {

	private static final long serialVersionUID = 1L;

	private static final String CONTEXT_KEY = "context";

	// Used by JaxB
	protected ContextAttribute() {}

	public ContextAttribute( final String value ) {
		super( CONTEXT_KEY, value );
	}

	public ContextAttribute( final String value, final String variableName ) {
		super( CONTEXT_KEY, value, variableName );
	}

	@Override
	public double getWeight() {
		return Attribute.IGNORE_WEIGHT;
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new ContextAttribute( (String) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new ContextAttribute( getValue(), variableName );
	}
}
