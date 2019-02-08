package de.retest.recheck.elementcollection;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import de.retest.recheck.ui.descriptors.Element;

@XmlAccessorType( XmlAccessType.FIELD )
public final class ComponentAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	public final Element element;
	public final String attributes;

	@SuppressWarnings( "unused" )
	private ComponentAttributes() {
		element = null;
		attributes = null;
	}

	public ComponentAttributes( final Element element, final Set<String> values ) {
		this.element = element;
		attributes = String.join( ", ", values );
	}

	public Element getElement() {
		return element;
	}

	public String getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return element + "[" + attributes + "]";
	}

	@Override
	public int hashCode() {
		return 31 * element.hashCode() + attributes.hashCode();
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( !(obj instanceof ComponentAttributes) ) {
			return false;
		}
		final ComponentAttributes other = (ComponentAttributes) obj;
		return attributes.equals( other.attributes ) && element.equals( other.element );
	}

	public Set<String> convertAttributes() {
		return new HashSet<>( asList( attributes.replaceAll( " ", "" ).split( "," ) ) );
	}

}
