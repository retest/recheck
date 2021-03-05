package de.retest.recheck.ui.descriptors;

import de.retest.recheck.ui.diff.AttributeDifference;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
