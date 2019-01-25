package de.retest.elementcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Joiner;

import de.retest.persistence.Persistable;
import de.retest.ui.TargetFinder;
import de.retest.ui.actions.ActionIdentifyingAttributes;
import de.retest.ui.descriptors.Attributes;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.review.ActionChangeSet;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ElementCollection extends Persistable implements Iterable<Element> {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 17;

	@XmlElement
	private final List<Element> elements = new ArrayList<>();

	// Since text and name could also be ignored, here we only need path and screenshot.
	@XmlElement
	@XmlJavaTypeAdapter( value = ExcludedAttributesAdapter.class )
	private HashMap<Element, Set<String>> attributes = new HashMap<>();

	public ElementCollection() {
		super( PERSISTENCE_VERSION );
	}

	public void add( final Element element ) {
		elements.add( element );
	}

	public void addAll( final List<Element> excludedComponentsList ) {
		elements.addAll( excludedComponentsList );
	}

	public boolean contains( final Element element ) {
		return getContained( element.getIdentifyingAttributes() ) != null;
	}

	public boolean contains( final IdentifyingAttributes identifyingAttributes ) {
		return getContained( identifyingAttributes ) != null;
	}

	private Element getContained( final IdentifyingAttributes identifyingAttributes ) {
		for ( final Element excludedComponent : elements ) {
			final double match = excludedComponent.getIdentifyingAttributes().match( identifyingAttributes );
			if ( match >= TargetFinder.MATCH_THRESHOLD ) {
				return excludedComponent;
			}
		}
		return null;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( o instanceof ElementCollection ) {
			return elements.equals( ((ElementCollection) o).elements )
					&& getAttributes().equals( ((ElementCollection) o).getAttributes() );
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * elements.hashCode() + getAttributes().hashCode();
	}

	public void remove( final Element element ) {
		elements.remove( getContained( element.getIdentifyingAttributes() ) );
	}

	public void remove( final IdentifyingAttributes identifyingAttributes ) {
		elements.remove( getContained( identifyingAttributes ) );
	}

	public int size() {
		return elements.size();
	}

	@Override
	public String toString() {
		return "ExcludedElements [" + Joiner.on( ", " ).join( elements ) + "]";
	}

	public List<Element> getElements() {
		return elements;
	}

	public List<Element> getElementsAndAttributes() {
		final List<Element> result = new ArrayList<>();
		result.addAll( elements );
		result.addAll( getAttributes().keySet() );
		return result;
	}

	public boolean containsThisOrParent( final ActionIdentifyingAttributes actionIdentifyingAttributes ) {
		for ( final Element element : elements ) {
			if ( actionIdentifyingAttributes.getIdentifyingAttributes().getPath()
					.startsWith( element.getIdentifyingAttributes().getPath() ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAttribute( final IdentifyingAttributes identifyingAttributes, final String key ) {
		for ( final Map.Entry<Element, Set<String>> ignoredAttributes : getAttributes().entrySet() ) {
			final String ignoredPath = ignoredAttributes.getKey().getIdentifyingAttributes().getPath();
			if ( ignoredPath.equals( identifyingAttributes.getPath() ) ) {
				return ignoredAttributes.getValue().contains( key );
			}
		}
		return false;
	}

	public boolean containsAttribute( final String elementRetestId, final String key ) {
		for ( final Map.Entry<Element, Set<String>> ignoredAttributes : getAttributes().entrySet() ) {
			final String ignoredRetestId = ignoredAttributes.getKey().getRetestId();
			if ( ignoredRetestId.equals( elementRetestId ) ) {
				return ignoredAttributes.getValue().contains( key );
			}
		}
		return false;
	}

	public void addAttribute( final Element element, final String key ) {
		Set<String> attributes = getAttributes().get( element );
		if ( attributes == null ) {
			attributes = new HashSet<>();
			getAttributes().put( element, attributes );
		}
		attributes.add( key );
	}

	public void remove( final String retestId, final IdentifyingAttributes identifyingAttributes, final String key ) {
		//TODO we need a solution for this case, where Element is not creatable without parent
		final RootElement parent =
				new RootElement( retestId, identifyingAttributes, new Attributes(), null, null, 0, null );
		final Element element = Element.create( retestId, parent, identifyingAttributes, new Attributes() );
		final Set<String> attributes = getAttributes().get( element );
		if ( attributes != null ) {
			attributes.remove( key );
		}
	}

	public Set<String> getAttributes( final Element component ) {
		return getAttributes().get( component );
	}

	public RootElement filterAttributes( final RootElement rootElement ) {
		final ActionChangeSet actionChangeSet = new ActionChangeSet();
		for ( final Map.Entry<Element, Set<String>> ignoredAttr : getAttributes().entrySet() ) {
			final Element actual =
					rootElement.getElement( ignoredAttr.getKey().getIdentifyingAttributes().getPathTyped() );
			if ( actual != null ) {
				for ( final String attributeKey : ignoredAttr.getValue() ) {
					if ( isIdentAttribute( attributeKey ) ) {
						final Object actualValue =
								actual.getIdentifyingAttributes().getAttribute( attributeKey ).getValue();
						if ( actualValue != null ) {
							actionChangeSet.getIdentAttributeChanges().add(
									ignoredAttr.getKey().getIdentifyingAttributes(),
									new AttributeDifference( attributeKey, actualValue.toString(), null ) );
						}
					} else {
						final Object actualValue = actual.getAttributes().get( attributeKey );
						if ( actualValue != null ) {
							actionChangeSet.getAttributesChanges().add( ignoredAttr.getKey().getIdentifyingAttributes(),
									new AttributeDifference( attributeKey, actualValue.toString(), null ) );
						}
					}
				}
			}
		}
		return rootElement.applyChanges( actionChangeSet );
	}

	protected boolean isIdentAttribute( final String attributeKey ) {
		return IdentifyingAttributes.isIdentifyingAttribute( attributeKey );
	}

	private HashMap<Element, Set<String>> getAttributes() {
		if ( attributes == null ) { // JAXB sets this to null when empty tag
			attributes = new HashMap<>();
		}
		return attributes;
	}

	@Override
	public Iterator<Element> iterator() {
		return elements.iterator();
	}

}
