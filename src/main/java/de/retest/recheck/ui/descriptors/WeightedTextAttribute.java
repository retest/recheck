package de.retest.recheck.ui.descriptors;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class WeightedTextAttribute extends TextAttribute {

	/**
	 * Text weight for similarity.
	 */
	public static final double TEXT_WEIGHT = 1.5;

	private static final long serialVersionUID = 1L;

	// Used by JaxB
	@SuppressWarnings( "unused" )
	private WeightedTextAttribute() {}

	public WeightedTextAttribute( final String key, final String value ) {
		this( key, value, null );
	}

	public WeightedTextAttribute( final String key, final String value, final String variableName ) {
		super( key, value, variableName );
	}

	@Override
	public double getWeight() {
		return TEXT_WEIGHT;
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new WeightedTextAttribute( getKey(), (String) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new WeightedTextAttribute( getKey(), getValue(), variableName );
	}
}
