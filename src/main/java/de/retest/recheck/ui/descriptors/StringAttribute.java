package de.retest.recheck.ui.descriptors;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import de.retest.recheck.util.StringSimilarity;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

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
			if ( "true".equalsIgnoreCase( value ) ) {
				return Boolean.TRUE;
			}
			if ( "false".equalsIgnoreCase( value ) ) {
				return Boolean.FALSE;
			}
			throw new ParameterParseException( "Value must be 'true' or 'false' (ignoring case).",
					new IllegalArgumentException() );
		}
	};

	private static final long serialVersionUID = 1L;

	@XmlValue
	@XmlValueExtension
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
		return value.replace( "]]>", "]]&gt;" );
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
		if ( !(other instanceof StringAttribute) || !other.getKey().equals( getKey() ) ) {
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
