package de.retest.recheck.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.util.StringSimilarity;

@XmlRootElement
public class TextAttribute extends StringAttribute {

	private static final long serialVersionUID = 1L;

	// Used by JaxB
	protected TextAttribute() {}

	public TextAttribute( final String key, final String value ) {
		this( key, value, null );
	}

	public TextAttribute( final String key, final String value, final String variableName ) {
		super( key, value != null ? value.trim() : null, variableName );
	}

	@Override
	public double match( final Attribute other ) {
		if ( !(other instanceof TextAttribute) ) {
			return NO_MATCH;
		}
		assert other.getKey().equals( getKey() );
		return StringSimilarity.textSimilarity( getValue(), ((StringAttribute) other).getValue() );
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new TextAttribute( getKey(), (String) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new TextAttribute( getKey(), getValue(), variableName );
	}
}
