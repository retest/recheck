package de.retest.recheck.ui.descriptors;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CodeLocAttribute extends TextAttribute {

	private static final long serialVersionUID = 1L;

	private static final String CODE_LOC_KEY = "codeLoc";

	// Used by JaxB
	@SuppressWarnings( "unused" )
	private CodeLocAttribute() {}

	public CodeLocAttribute( final String codeLoc ) {
		this( codeLoc, null );
	}

	public CodeLocAttribute( final String codeLoc, final String variableName ) {
		super( CODE_LOC_KEY, codeLoc != null && !"[]".equals( codeLoc ) ? codeLoc : null, variableName );
	}

	@Override
	public String getValue() {
		final Serializable value = super.getValue();
		return value != null && !"[]".equals( value ) ? (String) value : null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new CodeLocAttribute( (String) actual, getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new CodeLocAttribute( getValue(), variableName );
	}
}
