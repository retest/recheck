package de.retest.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.StringUtils;

import de.retest.util.StringSimilarity;

@XmlRootElement
public class StringAttribute extends ParameterizedAttribute {

	public static final ParameterType parameterTypeString = new ParameterType( "STRING" ) {
		@Override
		public String parse( final String value ) {
			return value;
		}
	};

	public static final ParameterType parameterTypeClass = new ParameterType( "CLASS_NAME" ) {
		@Override
		public Class<?> parse( final String value ) throws ParameterParseException {
			try {
				return Class.forName( value );
			} catch ( final Exception e ) {
				throw new ParameterParseException( "Value must be a fully qualified class on the classpath.", e );
			}
		}
	};

	public static final ParameterType parameterTypeInteger = new ParameterType( "INTEGER" ) {
		@Override
		public Integer parse( final String value ) throws ParameterParseException {
			try {
				return Integer.parseInt( value );
			} catch ( final Exception e ) {
				throw new ParameterParseException( "Value must be a valid integer.", e );
			}
		}
	};

	public static final ParameterType parameterTypeBoolean = new ParameterType( "BOOLEAN" ) {
		@Override
		public Boolean parse( final String value ) throws ParameterParseException {
			if ( value.equalsIgnoreCase( "true" ) ) {
				return Boolean.TRUE;
			}
			if ( value.equalsIgnoreCase( "false" ) ) {
				return Boolean.FALSE;
			}
			throw new ParameterParseException( "Value must be 'true' or 'false' (ignoring case).",
					new IllegalArgumentException() );
		}
	};

	private static final long serialVersionUID = 1L;

	@XmlValue
	private final String value;

	// Used by JaxB
	protected StringAttribute() {
		value = null;
	}

	public StringAttribute( final String key, final String value ) {
		this( key, value, null );
	}

	public StringAttribute( final String key, final String value, final String variableName ) {
		super( key, variableName );
		this.value = escape( value );
	}

	// This is really a bug in JAXB, as it should have been handled there.
	private static String escape( final String value ) {
		if ( value == null ) {
			return value;
		}
		return value.replaceAll( "]]>", "]]&gt;" );
	}

	@Override
	public String getValue() {
		if ( StringUtils.isEmpty( value ) ) {
			return null;
		}
		return value;
	}

	@Override
	public double match( final Attribute other ) {
		if ( !(other instanceof StringAttribute) ) {
			return NO_MATCH;
		}
		if ( !other.getKey().equals( getKey() ) ) {
			return NO_MATCH;
		}
		return StringSimilarity.simpleSimilarity( getValue(), ((StringAttribute) other).getValue() );
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new StringAttribute( getKey(), (String) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new StringAttribute( getKey(), getValue(), variableName );
	}

	@Override
	public ParameterType getType() {
		return parameterTypeString;
	}
}
