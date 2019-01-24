package de.retest.ui.descriptors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.diff.AttributeDifference;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class AdditionalAttributeDifference extends AttributeDifference {

	private static final long serialVersionUID = 1L;

	// for JAXB
	protected AdditionalAttributeDifference() {}

	public AdditionalAttributeDifference( final String key, final Attribute attribute ) {
		super( key, null, attribute );
	}

	@Override
	public Attribute applyChangeTo( final Attribute attribute ) {
		return (Attribute) getActual();
	}
}
