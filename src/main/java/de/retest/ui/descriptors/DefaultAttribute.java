package de.retest.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class DefaultAttribute extends ParameterizedAttribute {

	public static final ParameterType parameterTypeAttribute = new ParameterType( "ATTRIBUTE" ) {
		@Override
		public Object parse( final String value ) {
			return null;
		}
	};

	private static final long serialVersionUID = 1L;

	@XmlElement
	private final Serializable value;

	@SuppressWarnings( "unused" )
	protected DefaultAttribute() {
		value = null;
	}

	public DefaultAttribute( final String key, final Serializable value ) {
		this( key, value, null );
	}

	public DefaultAttribute( final String key, final Serializable value, final String variableName ) {
		super( key, variableName );
		this.value = value;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new DefaultAttribute( getKey(), actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new DefaultAttribute( getKey(), value, variableName );
	}

	@Override
	public ParameterType getType() {
		return parameterTypeAttribute;
	}

	@Override
	public double match( final Attribute other ) {
		if ( !(other instanceof DefaultAttribute) ) {
			return Attribute.NO_MATCH;
		}
		if ( value.equals( other.getValue() ) ) {
			return Attribute.FULL_MATCH;
		}
		return Attribute.NO_MATCH;
	}
}
