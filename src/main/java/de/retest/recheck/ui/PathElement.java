package de.retest.recheck.ui;

import java.io.Serializable;

import de.retest.recheck.persistence.StringInternerAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType( XmlAccessType.FIELD )
public class PathElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	@XmlJavaTypeAdapter( StringInternerAdapter.class )
	private final String elementName;

	@XmlElement
	private final int suffix;

	public PathElement( final String elementName, final int suffix ) {
		if ( elementName == null ) {
			throw new NullPointerException( "Element name must not be null." );
		}
		this.elementName = elementName.trim();
		if ( this.elementName.isEmpty() ) {
			throw new IllegalArgumentException( "Element name must not be empty." );
		}
		this.suffix = suffix;
	}

	public PathElement( final String elementName ) {
		this( elementName, 1 );
	}

	public String getElementName() {
		return elementName;
	}

	public int getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		return elementName + "[" + suffix + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (elementName == null ? 0 : elementName.hashCode());
		result = prime * result + suffix;
		return result;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final PathElement other = (PathElement) obj;
		return elementName.equals( other.elementName ) && suffix == other.suffix;
	}

	public static PathElement fromString( final String path ) {
		if ( !path.contains( "[" ) ) {
			return new PathElement( path );
		}
		return new PathElement( path.substring( 0, path.indexOf( '[' ) ),
				Integer.parseInt( path.substring( path.indexOf( '[' ) + 1, path.length() - 1 ) ) );
	}
}
