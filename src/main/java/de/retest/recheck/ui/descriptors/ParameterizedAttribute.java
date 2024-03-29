package de.retest.recheck.ui.descriptors;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class ParameterizedAttribute extends Attribute {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String variableName;

	// Used by JaxB
	protected ParameterizedAttribute() {
		variableName = null;
	}

	public ParameterizedAttribute( final String key, final String variableName ) {
		super( key );
		this.variableName = StringUtils.isNotEmpty( variableName ) ? variableName : null;
	}

	public String getVariableName() {
		return variableName;
	}

	public abstract ParameterizedAttribute applyVariableChange( final String variableName );

	public abstract ParameterType getType();
}
