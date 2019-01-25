package de.retest.elementcollection;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType( XmlAccessType.FIELD )
public class ExcludedAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<ComponentAttributes> attributes;

	@SuppressWarnings( "unused" )
	private ExcludedAttributes() {
		attributes = null;
	}

	public ExcludedAttributes( final List<ComponentAttributes> attributes ) {
		this.attributes = attributes;
	}

	public List<ComponentAttributes> getAttributes() {
		return attributes;
	}

}
