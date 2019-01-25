package de.retest.ui.descriptors;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SuffixAttribute extends StringAttribute {

	private static final long serialVersionUID = 1L;

	private static final String SUFFIX_KEY = "suffix";

	// Used by JaxB
	@SuppressWarnings( "unused" )
	private SuffixAttribute() {}

	public SuffixAttribute( final int value ) {
		this( value, null );
	}

	public SuffixAttribute( final int value, final String variableName ) {
		super( SUFFIX_KEY, Integer.toString( value ), variableName );
	}

	@Deprecated
	public SuffixAttribute( final String value ) {
		this( value, null );
	}

	@Deprecated
	public SuffixAttribute( final String value, final String variableName ) {
		super( SUFFIX_KEY, value, variableName );
	}

	@Override
	public boolean isVisible() {
		// Is not visible, because Path is shown
		return false;
	}

	@Override
	public Attribute applyChanges( final Serializable actual ) {
		return new SuffixAttribute( Integer.parseInt( (String) actual ), getVariableName() );
	}

	@Override
	public ParameterizedAttribute applyVariableChange( final String variableName ) {
		return new SuffixAttribute( Integer.parseInt( getValue() ), variableName );
	}
}
