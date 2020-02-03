package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.image.ImageUtils.image2Screenshot;
import static de.retest.recheck.ui.image.ImageUtils.screenshot2Image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.Marshaller;

import de.retest.recheck.persistence.xml.XmlTransformer;
import de.retest.recheck.ui.descriptors.AttributeUtil;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.image.ImageUtils;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.util.ChecksumCalculator;

public class ElementDifference implements Difference, Comparable<ElementDifference> {

	protected static final long serialVersionUID = 2L;

	protected final AttributesDifference attributesDifference;

	protected final LeafDifference identifyingAttributesDifference;

	protected List<ElementDifference> childDifferences = new ArrayList<>();

	protected final Screenshot expectedScreenshot;

	protected final Screenshot actualScreenshot;

	private Element element;

	public ElementDifference( final Element element, final AttributesDifference attributesDifference,
			final LeafDifference identifyingAttributesDifference, final Screenshot expectedScreenshot,
			final Screenshot actualScreenshot, final Collection<ElementDifference> childDifferences ) {
		this.element = element;
		this.attributesDifference = attributesDifference;
		this.identifyingAttributesDifference = identifyingAttributesDifference;
		this.expectedScreenshot = expectedScreenshot;
		this.actualScreenshot = actualScreenshot;
		this.childDifferences.addAll( childDifferences );
		Collections.sort( this.childDifferences );
	}

	public Screenshot mark( final Screenshot screenshot ) {
		if ( screenshot == null ) {
			return null;
		}
		final List<Rectangle> marks = new ArrayList<>();
		if ( childDifferences != null ) {
			for ( final Difference childDifference : childDifferences ) {
				for ( final ElementDifference compDiff : childDifference.getNonEmptyDifferences() ) {
					marks.add( AttributeUtil.getAbsoluteOutline( compDiff.getIdentifyingAttributes() ) );
				}
			}
		}
		return image2Screenshot( screenshot.getPersistenceId(),
				ImageUtils.mark( screenshot2Image( screenshot ), marks ) );
	}

	public static ElementDifference
			getCopyWithFlattenedChildDifferenceHierarchy( final ElementDifference elementDifference ) {
		ElementDifference result = elementDifference;
		while ( result.childDifferences.size() == 1 && result.identifyingAttributesDifference == null
				&& result.attributesDifference == null ) {
			result = result.childDifferences.iterator().next();
		}
		return result;
	}

	public List<ElementDifference> getChildDifferences() {
		return Collections.unmodifiableList( childDifferences );
	}

	public List<Difference> getImmediateDifferences() {
		final List<Difference> differences = new ArrayList<>();
		if ( identifyingAttributesDifference != null ) {
			differences.add( identifyingAttributesDifference );
		}
		if ( attributesDifference != null ) {
			differences.add( attributesDifference );
		}
		return differences;
	}

	public List<AttributeDifference> getAttributeDifferences() {
		final List<AttributeDifference> differences = new ArrayList<>();
		if ( identifyingAttributesDifference instanceof IdentifyingAttributesDifference ) {
			final IdentifyingAttributesDifference identifyingAttributesDifference =
					(IdentifyingAttributesDifference) this.identifyingAttributesDifference;
			differences.addAll( identifyingAttributesDifference.getAttributeDifferences() );
		}
		if ( attributesDifference != null ) {
			differences.addAll( attributesDifference.getDifferences() );
		}
		return differences;
	}

	public String getIdentifier() {
		String result = getIdentifyingAttributes().identifier();
		if ( identifyingAttributesDifference != null ) {
			result += getSumIdentifier( identifyingAttributesDifference.getNonEmptyDifferences() );
		}
		if ( attributesDifference != null ) {
			result += attributesDifference.getIdentifier();
		}
		return ChecksumCalculator.getInstance().sha256( result );
	}

	private static String getSumIdentifier( final Collection<ElementDifference> differences ) {
		return differences.stream() //
				.map( ElementDifference::getIdentifier ) //
				.collect( Collectors.collectingAndThen( Collectors.joining( " # " ),
						ChecksumCalculator.getInstance()::sha256 ) );
	}

	public boolean hasAttributesDifferences() {
		return attributesDifference != null && attributesDifference.size() > 0;
	}

	public boolean hasIdentAttributesDifferences() {
		return identifyingAttributesDifference instanceof IdentifyingAttributesDifference;
	}

	public boolean isInsertionOrDeletion() {
		return identifyingAttributesDifference instanceof InsertedDeletedElementDifference;
	}

	public boolean hasChildDifferences() {
		return !childDifferences.isEmpty();
	}

	public boolean isInsertion() {
		return isInsertionOrDeletion()
				&& ((InsertedDeletedElementDifference) identifyingAttributesDifference).isInserted();
	}

	public boolean isDeletion() {
		return isInsertionOrDeletion()
				&& !((InsertedDeletedElementDifference) identifyingAttributesDifference).isInserted();
	}

	/**
	 * @return <code>true</code> if there any <em>own</em> differences (i.e. attributes, identifying attributes and
	 *         inserted or deleted), excluding child differences.
	 */
	public boolean hasAnyDifference() {
		return hasAttributesDifferences() || hasIdentAttributesDifferences() || isInsertionOrDeletion();
	}

	// For Difference

	@Override
	public int size() {
		if ( identifyingAttributesDifference != null || attributesDifference != null ) {
			return 1;
		}
		if ( hasChildDifferences() ) {
			int size = 0;
			for ( final Difference difference : childDifferences ) {
				size += difference.size();
			}
			return size;
		}
		return 0;
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		final List<ElementDifference> result = new ArrayList<>();
		if ( identifyingAttributesDifference != null || attributesDifference != null ) {
			result.add( this );
		}
		for ( final Difference childDifference : childDifferences ) {
			result.addAll( childDifference.getNonEmptyDifferences() );
		}
		return result;
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		final List<ElementDifference> differences = new ArrayList<>();
		differences.add( this );
		for ( final ElementDifference childDifference : childDifferences ) {
			differences.addAll( childDifference.getElementDifferences() );
		}
		return differences;
	}

	@Override
	public String toString() {
		if ( identifyingAttributesDifference != null ) {
			return getIdentifyingAttributes().toString() //
					+ ":\n at: " + getIdentifyingAttributes().getPath() //
					+ ":\n\t" + identifyingAttributesDifference;
		}
		if ( attributesDifference != null ) {
			final String differences = attributesDifference.getDifferences().stream() //
					.map( Object::toString ) //
					.collect( Collectors.joining( "\n\t" ) );
			return getIdentifyingAttributes().toString() //
					+ ":\n at: " + getIdentifyingAttributes().getPath() //
					+ ":\n\t" + differences;
		}
		if ( hasChildDifferences() ) {
			if ( size() > 50 ) {
				final StringBuilder result = new StringBuilder();
				int diffCnt = 0;
				final Iterator<ElementDifference> diffIter = childDifferences.iterator();
				while ( diffCnt < 50 && diffIter.hasNext() ) {
					final Difference difference = diffIter.next();
					diffCnt += difference.size();
					result.append( difference.toString() + ", " );
				}
				return result.substring( 0, result.length() - 2 );
			}
			return childDifferences.toString();
		}
		return "noDifferences: " + getIdentifyingAttributes().toString();
	}

	// For Comparable

	@Override
	public int compareTo( final ElementDifference other ) {
		if ( getIdentifyingAttributes() == null || other.getIdentifyingAttributes() == null ) {
			throw new IllegalStateException( "Identifying attributes may not be null. Loaded lightweight XML?" );
		}
		return getIdentifyingAttributes().compareTo( other.getIdentifyingAttributes() );
	}

	// For JAXB
	protected ElementDifference() {
		attributesDifference = null;
		identifyingAttributesDifference = null;
		expectedScreenshot = null;
		actualScreenshot = null;
	}

	void beforeMarshal( final Marshaller m ) {
		if ( XmlTransformer.isLightweightMarshaller( m ) && identifyingAttributesDifference == null
				&& attributesDifference == null ) {
			final List<ElementDifference> childDifferences = getClippedNonEmptyChildren();
			childDifferences.remove( this );
			this.childDifferences = childDifferences;
		}
	}

	private List<ElementDifference> getClippedNonEmptyChildren() {
		final List<ElementDifference> result = new ArrayList<>();
		if ( identifyingAttributesDifference != null || attributesDifference != null ) {
			result.add( this );
		} else {
			for ( final ElementDifference childDifference : childDifferences ) {
				result.addAll( childDifference.getClippedNonEmptyChildren() );
			}
		}
		return result;
	}

	// Getters

	public IdentifyingAttributes getIdentifyingAttributes() {
		return element.getIdentifyingAttributes();
	}

	public String getRetestId() {
		return element.getRetestId();
	}

	public AttributesDifference getAttributesDifference() {
		return attributesDifference;
	}

	public LeafDifference getIdentifyingAttributesDifference() {
		return identifyingAttributesDifference;
	}

	public Screenshot getExpectedScreenshot() {
		return expectedScreenshot;
	}

	public Screenshot getActualScreenshot() {
		return actualScreenshot;
	}

	public Element getElement() {
		return element;
	}
}
