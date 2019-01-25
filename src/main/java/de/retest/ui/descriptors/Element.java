package de.retest.ui.descriptors;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import de.retest.ui.Path;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.image.Screenshot;
import de.retest.ui.review.ActionChangeSet;
import de.retest.util.RetestIdUtil;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Element implements Serializable, Comparable<Element> {

	protected static final long serialVersionUID = 2L;

	@XmlAttribute
	protected final String retestId;

	@XmlInverseReference( mappedBy = "containedElements" )
	private final Element parent;

	@XmlJavaTypeAdapter( IdentifyingAttributesAdapter.class )
	@XmlElement
	protected final IdentifyingAttributes identifyingAttributes;

	@XmlJavaTypeAdapter( StateAttributesAdapter.class )
	@XmlElement
	protected final Attributes attributes;

	@XmlElement
	@XmlJavaTypeAdapter( RenderContainedElementsAdapter.class )
	protected final List<Element> containedElements;

	@XmlElement
	protected Screenshot screenshot;

	@XmlTransient
	private transient Integer hashCodeCache;

	// Warning: Only to be used by JAXB!
	protected Element() {
		retestId = "";
		parent = null;
		identifyingAttributes = null;
		attributes = null;
		containedElements = new ArrayList<>();
	}

	Element( final String retestId, final Element parent, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final Screenshot screenshot ) {
		RetestIdUtil.validate( retestId, identifyingAttributes );
		this.retestId = retestId;
		this.parent = Objects.requireNonNull( parent, "Parent must not be null" );
		this.identifyingAttributes =
				Objects.requireNonNull( identifyingAttributes, "IdentifyingAttributes must not be null" );
		this.attributes = Objects.requireNonNull( attributes, "Attributes must not be null" );
		containedElements = new ArrayList<>();
		this.screenshot = screenshot;
	}

	public static Element create( final String retestId, final Element parent,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes ) {
		return new Element( retestId, parent, identifyingAttributes, attributes, null );
	}

	public static Element create( final String retestId, final Element parent,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot ) {
		return new Element( retestId, parent, identifyingAttributes, attributes, screenshot );
	}

	public Element applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}

		final IdentifyingAttributes newIdentAttributes = identifyingAttributes
				.applyChanges( actionChangeSet.getIdentAttributeChanges().getAll( identifyingAttributes ) );

		final Attributes newAttributes =
				attributes.applyChanges( actionChangeSet.getAttributesChanges().getAll( identifyingAttributes ) );
		final List<Element> newContainedElements = createNewElementList( actionChangeSet, newIdentAttributes );

		final Element element = Element.create( retestId, this, newIdentAttributes, newAttributes );
		element.addChildren( newContainedElements );
		return element;
	}

	protected List<Element> createNewElementList( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes ) {
		List<Element> newContainedElements = containedElements;
		newContainedElements = removeDeleted( actionChangeSet, newContainedElements );
		newContainedElements =
				applyChangesToContainedElements( actionChangeSet, newIdentAttributes, newContainedElements );
		newContainedElements = addInserted( actionChangeSet, newIdentAttributes, newContainedElements );
		return newContainedElements;
	}

	private List<Element> removeDeleted( final ActionChangeSet actionChangeSet,
			final List<Element> oldContainedElements ) {
		final Set<IdentifyingAttributes> deletedChanges = actionChangeSet.getDeletedChanges();
		final List<Element> newContainedElements = new ArrayList<>( oldContainedElements.size() );

		for ( final Element oldElement : oldContainedElements ) {
			if ( !deletedChanges.contains( oldElement.getIdentifyingAttributes() ) ) {
				newContainedElements.add( oldElement );
			}
		}

		return newContainedElements;
	}

	private List<Element> applyChangesToContainedElements( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> oldContainedElements ) {
		final List<Element> newContainedElements = new ArrayList<>( oldContainedElements.size() );

		for ( final Element oldElement : oldContainedElements ) {
			addPathChangeToChangeSet( actionChangeSet, newIdentAttributes, oldElement );
			newContainedElements.add( oldElement.applyChanges( actionChangeSet ) );
		}

		return newContainedElements;
	}

	private void addPathChangeToChangeSet( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final Element oldElement ) {
		if ( ObjectUtils.notEqual( identifyingAttributes.getPathTyped(), newIdentAttributes.getPathTyped() ) ) {
			final Path oldPath = oldElement.identifyingAttributes.getPathTyped();
			final Path newPath = Path.fromString( newIdentAttributes.getPath() + Path.PATH_SEPARATOR
					+ oldElement.identifyingAttributes.getPathElement().toString() );

			actionChangeSet.getIdentAttributeChanges().add( oldElement.identifyingAttributes,
					new AttributeDifference( "path", oldPath, newPath ) );
		}
	}

	private List<Element> addInserted( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> newContainedElements ) {
		for ( final Element insertedElement : actionChangeSet.getInsertedChanges() ) {
			if ( isParent( newIdentAttributes, insertedElement.identifyingAttributes ) ) {
				newContainedElements.add( insertedElement );
			}
		}
		return newContainedElements;
	}

	public int countAllContainedElements() {
		// count current elements!
		int result = 1;
		for ( final Element element : containedElements ) {
			result += element.countAllContainedElements();
		}
		return result;
	}

	public Element getElement( final Path path ) {
		final Path thisPath = getIdentifyingAttributes().getPathTyped();
		if ( thisPath.equals( path ) ) {
			return this;
		}
		if ( thisPath.isParent( path ) ) {
			for ( final Element element : containedElements ) {
				final Element contained = element.getElement( path );
				if ( contained != null ) {
					return contained;
				}
			}
		}
		return null;
	}

	public IdentifyingAttributes getIdentifyingAttributes() {
		return identifyingAttributes;
	}

	public List<Element> getContainedElements() {
		return containedElements;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public String getRetestId() {
		return retestId;
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

	public void setScreenshot( final Screenshot screenshot ) {
		if ( screenshot == null && this.screenshot != null ) {
			throw new RuntimeException( "Screenshot can only be replaced, not deleted." );
		}
		this.screenshot = screenshot;
	}

	public boolean hasContainedElements() {
		return !containedElements.isEmpty();
	}

	private static boolean isParent( final IdentifyingAttributes parentIdentAttributes,
			final IdentifyingAttributes containedIdentAttributes ) {
		return parentIdentAttributes.getPathTyped().equals( containedIdentAttributes.getParentPathTyped() );
	}

	@Transient
	public Element getParent() {
		return parent;
	}

	public void addChildren( final Element... children ) {
		containedElements.addAll( Arrays.asList( children ) );
	}

	public void addChildren( final List<Element> children ) {
		containedElements.addAll( children );
	}

	@Override
	public int compareTo( final Element other ) {
		final int result = identifyingAttributes.compareTo( other.getIdentifyingAttributes() );
		if ( result != 0 ) {
			return result;
		}
		return attributes.compareTo( other.getAttributes() );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		final Element other = (Element) obj;
		if ( !identifyingAttributes.equals( other.identifyingAttributes ) ) {
			return false;
		}
		if ( !attributes.equals( other.attributes ) ) {
			return false;
		}
		return containedElements.equals( other.containedElements );
	}

	@Override
	public int hashCode() {
		if ( hashCodeCache == null ) {
			hashCodeCache = Objects.hash( identifyingAttributes, attributes, containedElements );
		}
		return hashCodeCache;
	}

	@Override
	public String toString() {
		return identifyingAttributes.toString();
	}
}
