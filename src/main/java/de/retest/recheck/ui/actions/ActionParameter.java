package de.retest.recheck.ui.actions;

import java.io.Serializable;
import java.util.Objects;

import de.retest.recheck.ui.descriptors.ParameterType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActionParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String name;

	@XmlAttribute
	private final String value;

	@XmlAttribute
	private final String type;

	@XmlAttribute
	private final String variableName;

	@XmlAttribute
	private final String attributeClass;

	// For JAXB
	@SuppressWarnings( "unused" )
	private ActionParameter() {
		name = null;
		value = null;
		type = null;
		variableName = null;
		attributeClass = null;
	}

	public ActionParameter( final String name, final String value, final String type ) {
		this( name, value, type, null );
	}

	public ActionParameter( final String name, final String value, final ParameterType type ) {
		this( name, value, type, null );
	}

	public ActionParameter( final String name, final String value, final ParameterType type,
			final String variableName ) {
		this( name, value, type, variableName, null );
	}

	public ActionParameter( final String name, final String value, final String type, final String variableName ) {
		this( name, value, type, variableName, null );
	}

	public ActionParameter( final String name, final String value, final ParameterType type, final String variableName,
			final String attributeClass ) {
		this( name, value, type.toString(), variableName, attributeClass );
	}

	public ActionParameter( final String name, final String value, final String type, final String variableName,
			final String attributeClass ) {
		if ( name == null ) {
			throw new IllegalArgumentException( "Name must not be null." );
		}
		this.name = name;
		this.value = value;
		if ( type == null ) {
			throw new IllegalArgumentException( "Type must not be null." );
		}
		this.type = type;
		this.variableName = variableName;
		this.attributeClass = attributeClass;
	}

	public ActionParameter setValue( final String value ) {
		return new ActionParameter( name, value, type, variableName, attributeClass );
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getAttributeClass() {
		return attributeClass;
	}

	@Override
	public String toString() {
		return "ActionParameter[" + name + "(" + type + ")=" + value
				+ (variableName != null ? "(${" + variableName + "})" : "") + "]";
	}

	@Override
	public int hashCode() {
		// ActionParameter are solely defined by name
		return name.hashCode();
	}

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( !(object instanceof ActionParameter) ) {
			return false;
		}
		final ActionParameter other = (ActionParameter) object;
		if ( !name.equals( other.getName() ) //
				|| !Objects.equals( type, other.type ) //
				|| !Objects.equals( value, other.getValue() ) ) {
			return false;
		}
		return true;
	}
}
