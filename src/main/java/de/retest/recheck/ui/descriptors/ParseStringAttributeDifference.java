package de.retest.recheck.ui.descriptors;

import java.io.Serializable;

import de.retest.recheck.ui.diff.AttributeDifference;

public class ParseStringAttributeDifference extends AttributeDifference {

	private static final long serialVersionUID = 1L;

	public ParseStringAttributeDifference( final ParameterizedAttribute attribute, final String actual ) {
		super( attribute.getKey(), attribute.getValue(), actual );
	}

	@Override
	public Attribute applyChangeTo( final Attribute attribute ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final String actual = (String) getActual();
			try {
				final ParameterizedAttribute param = (ParameterizedAttribute) attribute;
				final ParameterType type = param.getType();
				final Serializable value = (Serializable) type.parse( actual );
				return attribute.applyChanges( value );
			} catch ( final Exception e ) {
				return attribute;
			}
		}
		return attribute;
	}
}
