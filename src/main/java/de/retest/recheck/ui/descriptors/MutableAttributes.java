package de.retest.recheck.ui.descriptors;

import java.util.TreeMap;

import de.retest.recheck.ui.image.Screenshot;

public class MutableAttributes {
	final TreeMap<String, Object> attributes;

	public MutableAttributes() {
		attributes = new TreeMap<>();
	}

	public MutableAttributes( final Attributes attributes ) {
		this.attributes = new TreeMap<>( attributes.getMap() );
	}

	public Attributes immutable() {
		return new Attributes( this );
	}

	public void put( final String name, final String criterion ) {
		attributes.put( name, criterion );
	}

	public void put( final String name, final Integer criterion ) {
		attributes.put( name, criterion );
	}

	public void put( final String name, final Boolean criterion ) {
		attributes.put( name, criterion );
	}

	public Object get( final String name ) {
		return attributes.get( name );
	}

	public void put( final Screenshot screenshot ) {
		attributes.put( Attributes.SCREENSHOT, screenshot );
	}
}
