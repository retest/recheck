package de.retest.ui.descriptors;

import static de.retest.util.ObjectUtil.nextHashCode;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.retest.ui.Path;
import de.retest.ui.PathElement;
import de.retest.ui.diff.AttributeDifference;
import de.retest.util.ChecksumCalculator;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class IdentifyingAttributes implements Serializable, Comparable<IdentifyingAttributes> {

	// "suffix" implicitly contained via "path"
	private static final List<String> identifyingAttributes = Collections.unmodifiableList(
			Arrays.asList( "path", "type", "name", "text", "codeLoc", "x", "y", "height", "width", "context" ) );

	/**
	 * Sum of all weights.
	 */
	public static final double PERFECT_SIMILARITY = 3.0;

	private static final long serialVersionUID = 1L;

	@XmlElement
	@XmlJavaTypeAdapter( AttributesAdapter.class )
	private final SortedMap<String, Attribute> attributes = new TreeMap<>();

	private transient String parentPathCache;

	protected IdentifyingAttributes() {
		// Only for JAXB
	}

	public IdentifyingAttributes( final Collection<Attribute> attributes ) {
		for ( final Attribute attribute : attributes ) {
			this.attributes.put( attribute.getKey(), attribute );
		}
	}

	public static Collection<Attribute> createList( final Path path, String type ) {
		if ( type == null ) {
			throw new NullPointerException( "Type must not be null." );
		}
		type = type.trim();
		if ( type.isEmpty() ) {
			throw new IllegalArgumentException( "Type must not be empty." );
		}
		return new ArrayList<>( Arrays.asList( new PathAttribute( path ), //
				new StringAttribute( "type", type ), //
				new SuffixAttribute( path.getElement().getSuffix() ) )//
		);
	}

	public static IdentifyingAttributes create( final Path path, final Class<?> type ) {
		return create( path, type.getName() );
	}

	public static IdentifyingAttributes create( final Path path, final String type ) {
		return new IdentifyingAttributes( createList( path, type ) );
	}

	public String getType() {
		return get( "type" );
	}

	public OutlineAttribute getOutline() {
		return (OutlineAttribute) getAttribute( "outline" );
	}

	public Rectangle getOutlineRectangle() {
		final OutlineAttribute outlineAttribute = getOutline();
		if ( outlineAttribute == null ) {
			return null;
		}
		return outlineAttribute.getValue();
	}

	public String getSimpleType() {
		return getType().substring( getType().lastIndexOf( '.' ) + 1 );
	}

	public String getPath() {
		return getPathTyped().toString();
	}

	public Path getPathTyped() {
		return ((PathAttribute) attributes.get( "path" )).getValue();
	}

	public String getParentPath() {
		if ( parentPathCache == null ) {
			parentPathCache = getPathTyped().getParentPath() == null ? "" : getPathTyped().getParentPath().toString();
		}
		return parentPathCache;
	}

	public Path getParentPathTyped() {
		return getPathTyped().getParentPath();
	}

	public PathElement getPathElement() {
		return getPathTyped().getElement();
	}

	public String toFullString() {
		return getValuesForFullString().stream() //
				.filter( Objects::nonNull ) //
				.collect( Collectors.joining( " # " ) );
	}

	public String identifier() {
		return ChecksumCalculator.getInstance().sha256( toFullString() );
	}

	protected List<String> getValuesForFullString() {
		return Arrays.asList( getParentPath(), getType(), getSuffix() );
	}

	@Override
	public int compareTo( final IdentifyingAttributes other ) {
		// beware: sort order makes a difference!
		for ( final Attribute attribute : attributes.values() ) {
			final int result = attribute.compareTo( other.getAttribute( attribute.getKey() ) );
			if ( result != Attribute.COMPARE_EQUAL ) {
				return result;
			}
		}
		return Attribute.COMPARE_EQUAL;
	}

	public double match( final IdentifyingAttributes other ) {
		double result = 0.0;
		double unifyingFactor = 0.0;
		final Set<Attribute> otherAttributes = new HashSet<>( other.attributes.values() );
		for ( final Attribute attribute : attributes.values() ) {
			unifyingFactor += attribute.getWeight();
			final Attribute otherAttribute = other.getAttribute( attribute.getKey() );
			otherAttributes.remove( otherAttribute );
			if ( otherAttribute != null ) {
				result += attribute.getWeight() * attribute.match( otherAttribute );
			}
		}
		for ( final Attribute attribute : otherAttributes ) {
			unifyingFactor += attribute.getWeight();
		}
		if ( unifyingFactor == 0.0 ) {
			throw new ArithmeticException( "Cannot divide with a unifying factor of 0.0" );
		}
		result = result / unifyingFactor;
		assert result >= 0.0 && result <= 1.0 : "Match result " + result + " should be in [0,1].";
		return result;
	}

	@Override
	public int hashCode() {
		int result = 0;
		final Collection<Attribute> values = attributes.values();
		for ( final Attribute attribute : values ) {
			if ( attribute.getWeight() > 0 ) {
				result = nextHashCode( result, attribute.hashCode() );
			}
		}
		return result;
	}

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( !(object instanceof IdentifyingAttributes) ) {
			return false;
		}
		final IdentifyingAttributes other = (IdentifyingAttributes) object;
		return match( other ) > 0.999999999999999;
	}

	@Override
	public String toString() {
		final String type = getType();
		final String text = get( "text" );
		if ( type == null ) {
			return "";
		}
		String result = type;
		if ( type.lastIndexOf( '.' ) > -1 ) {
			result = type.substring( type.lastIndexOf( '.' ) + 1, type.length() );
		}
		if ( text != null ) {
			result += " [" + text + "]";
		}
		return result;
	}

	public String getSuffix() {
		return get( "suffix" );
	}

	public <T> T get( final String key ) {
		final Attribute attribute = attributes.get( key );
		if ( attribute == null ) {
			return null;
		}
		@SuppressWarnings( "unchecked" )
		final T value = (T) attribute.getValue();
		return value;
	}

	public String getContext() {
		return get( "context" );
	}

	public IdentifyingAttributes applyChanges( final Set<AttributeDifference> attributeChanges ) {
		if ( attributeChanges.isEmpty() ) {
			return this;
		}
		final HashMap<String, Attribute> newAttributes = new HashMap<>( attributes );
		for ( final AttributeDifference attributeDifference : attributeChanges ) {
			final String key = attributeDifference.getKey();
			final Attribute attribute = attributes.get( key );
			newAttributes.put( key, attributeDifference.applyChangeTo( attribute ) );
		}

		return newInstance( newAttributes.values() );
	}

	protected IdentifyingAttributes newInstance( final Collection<Attribute> attributes ) {
		return new IdentifyingAttributes( attributes );
	}

	public Attribute getAttribute( final String key ) {
		return attributes.get( key );
	}

	public List<Attribute> getAttributes() {
		final ArrayList<Attribute> result = new ArrayList<>( attributes.values() );
		Collections.sort( result );
		return result;
	}

	public static boolean isIdentifyingAttribute( final String key ) {
		return identifyingAttributes.contains( key );
	}

}
