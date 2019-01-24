package de.retest.ui.descriptors;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ParameterType {

	private final String toString;

	public ParameterType( final String toString ) {
		this.toString = toString;
	}

	public abstract Object parse( final String value ) throws ParameterParseException;

	public boolean canParse( final String value ) {
		try {
			parse( value );
			return true;
		} catch ( final ParameterParseException e ) {
			return false;
		}
	}

	@Override
	public String toString() {
		return toString;
	}

	@Override
	public int hashCode() {
		return toString.hashCode();
	}

	@Override
	public boolean equals( final Object other ) {
		if ( other == null ) {
			return false;
		}
		if ( !(other instanceof ParameterType) ) {
			return false;
		}
		return Objects.equals( toString, other.toString() );
	}

	private static final Map<String, ParameterType> registeredParameterTypes = new HashMap<>();

	public static void registerParameterType( final ParameterType type ) {
		registeredParameterTypes.put( type.toString, type );
	}

	public static ParameterType getType( final String type ) {
		final ParameterType parameterType = registeredParameterTypes.get( type );
		if ( parameterType != null ) {
			return parameterType;
		}
		// If this comes in a test: was the ParameterType registered?
		throw new IllegalStateException( "No ParameterType registered for parameters of type " + type );
	}

	public static void registerStdParameterTypes() {
		registerParameterType( PathAttribute.parameterTypePath );
		registerParameterType( DefaultAttribute.parameterTypeAttribute );
		registerParameterType( StringAttribute.parameterTypeString );
		registerParameterType( StringAttribute.parameterTypeBoolean );
		registerParameterType( StringAttribute.parameterTypeInteger );
		registerParameterType( StringAttribute.parameterTypeClass );
	}
}
