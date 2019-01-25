package de.retest.ui.descriptors;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.image.Screenshot;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Attributes implements Iterable<Map.Entry<String, Object>>, Serializable, Comparable<Attributes> {

	public static final String TEXT = "text";
	public static final String TOOLTIP = "tooltip";
	public static final String SCREENSHOT = "screenshot";

	private static final long serialVersionUID = 1L;

	// This should be an unmodifiable Map<String, Serializable> but there is a bug in Eclipse MOXy.
	@XmlElement
	private final TreeMap<String, Object> attributes;
	public static final String ENABLED = "enabled";
	public static final String BACKGROUND_COLOR = "backgroundColor";
	public static final String FOREGROUND_COLOR = "foregroundColor";
	public static final String FONT_SIZE = "fontSize";
	public static final String FONT_TYPE = "fontType";
	public static final String FONT_FAMILY = "fontFamily";

	@XmlElement
	protected final Screenshot screenshot;

	public Attributes() {
		attributes = new TreeMap<>();
		screenshot = null;
	}

	Attributes( final MutableAttributes other ) {
		attributes = new TreeMap<>( other.attributes );
		screenshot = (Screenshot) other.get( SCREENSHOT );
		if ( screenshot != null ) {
			attributes.put( SCREENSHOT, screenshot.getPersistenceId() );
		}
	}

	public Object get( final String name ) {
		if ( name.equals( SCREENSHOT ) ) {
			return screenshot;
		}
		return attributes.get( name );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public int compareTo( final Attributes other ) {
		final TreeSet<String> allKeys = new TreeSet<>( attributes.keySet() );
		allKeys.addAll( other.attributes.keySet() );
		for ( final String key : allKeys ) {
			final Comparable localValue = (Comparable) attributes.get( key );
			final Comparable otherValue = (Comparable) other.get( key );
			if ( localValue == null ) {
				return -1 * otherValue.compareTo( localValue );
			}
			final int result = localValue.compareTo( otherValue );
			if ( result != 0 ) {
				return result;
			}
		}
		return 0;
	}

	@Override
	public Iterator<Map.Entry<String, Object>> iterator() {
		return Collections.unmodifiableSet( attributes.entrySet() ).iterator();
	}

	@Override
	public int hashCode() {
		return attributes.hashCode();
	}

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( object instanceof Attributes ) {
			final Attributes other = (Attributes) object;
			return attributes.equals( other.attributes );
		}
		return false;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}

	// let's not spread the eclipse moxy bug out into the rest of the system but
	// instead convert the map here:
	public Map<String, ? extends Serializable> getMap() {
		final TreeMap<String, Serializable> result = new TreeMap<>();
		for ( final Entry<String, Object> entry : attributes.entrySet() ) {
			// TODO how to handle any class cast exceptions?
			result.put( entry.getKey(), (Serializable) entry.getValue() );
		}
		if ( screenshot != null ) {
			result.put( SCREENSHOT, screenshot );
		}
		return result;
	}

	public int size() {
		return attributes.size();
	}

	public Attributes applyChanges( final Set<AttributeDifference> attributeChanges ) {
		final MutableAttributes result = new MutableAttributes( this );
		for ( final AttributeDifference attributeDifference : attributeChanges ) {
			if ( Objects.equals( attributes.get( attributeDifference.getKey() ), attributeDifference.getExpected() ) ) {
				if ( attributeDifference.getActual() == null ) {
					result.attributes.remove( attributeDifference.getKey() );
				} else {
					result.attributes.put( attributeDifference.getKey(), attributeDifference.getActual() );
				}
				continue;
			}
			if ( attributeDifference.getKey().equals( Attributes.SCREENSHOT ) ) {
				result.put( (Screenshot) attributeDifference.getActual() );
			}
		}
		return result.immutable();
	}
}
