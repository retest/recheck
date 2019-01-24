package de.retest.ui.diff;

import static de.retest.ui.image.ImageUtils.image2Screenshot;
import static de.retest.ui.image.ImageUtils.screenshot2Image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.IdentifyingAttributesAdapter;
import de.retest.ui.image.ImageUtils;
import de.retest.ui.image.Screenshot;
import de.retest.util.ChecksumCalculator;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
@XmlJavaTypeAdapter( ElementDifferenceAdapter.class )
public class ElementDifference implements Difference, Comparable<ElementDifference> {

	protected static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String retestId;

	@XmlJavaTypeAdapter( IdentifyingAttributesAdapter.class )
	@XmlElement
	protected final IdentifyingAttributes identifyingAttributes;

	@XmlElement
	protected final AttributesDifference attributesDifference;

	@XmlElement
	protected final LeafDifference identifyingAttributesDifference;

	@XmlElement( name = "elementDifference" )
	@XmlElementWrapper( name = "childDifferences" )
	protected Collection<ElementDifference> childDifferences = new ArrayList<>();

	@XmlElement
	protected final Screenshot expectedScreenshot;
	@XmlElement
	protected final Screenshot actualScreenshot;

	protected ElementDifference() {
		// for JAXB
		identifyingAttributes = null;
		attributesDifference = null;
		identifyingAttributesDifference = null;
		expectedScreenshot = null;
		actualScreenshot = null;
		retestId = null;
	}

	public ElementDifference( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final AttributesDifference attributesDifference, final LeafDifference identifyingAttributesDifference,
			final Screenshot expectedScreenshot, final Screenshot actualScreenshot,
			final Collection<ElementDifference> childDifferences ) {
		this.identifyingAttributes = identifyingAttributes;
		this.attributesDifference = attributesDifference;
		this.identifyingAttributesDifference = identifyingAttributesDifference;
		this.expectedScreenshot = expectedScreenshot;
		this.actualScreenshot = actualScreenshot;
		this.childDifferences.addAll( childDifferences );

		this.retestId = retestId;
	}

	@Override
	public String toString() {
		if ( identifyingAttributesDifference != null ) {
			return identifyingAttributes.toString() //
					+ ":\n at: " + identifyingAttributes.getPath() //
					+ ":\n\t" + identifyingAttributesDifference;
		}
		if ( attributesDifference != null ) {
			return identifyingAttributes.toString() //
					+ ":\n at: " + identifyingAttributes.getPath() //
					+ ":\n\t" + attributesDifference.getAttributes().stream().map( AttributeDifference::toString )
							.collect( Collectors.joining( "\n\t" ) );

		}
		if ( !childDifferences.isEmpty() ) {
			if ( size() > 50 ) {
				String result = "";
				int diffCnt = 0;
				final Iterator<ElementDifference> diffIter = childDifferences.iterator();
				while ( diffCnt < 50 && diffIter.hasNext() ) {
					final Difference difference = diffIter.next();
					diffCnt += difference.size();
					result += difference.toString() + ", ";
				}
				return result.substring( 0, result.length() - 2 );
			}
			return childDifferences.toString();
		}
		return "noDifferences: " + identifyingAttributes.toString();
	}

	@Override
	public int size() {
		if ( identifyingAttributesDifference != null || attributesDifference != null ) {
			return 1;
		}
		if ( !childDifferences.isEmpty() ) {
			int size = 0;
			for ( final Difference difference : childDifferences ) {
				size += difference.size();
			}
			return size;
		}
		return 0;
	}

	public IdentifyingAttributes getIdentifyingAttributes() {
		return identifyingAttributes;
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

	public static ElementDifference
			getCopyWithFlattenedChildDifferenceHierarchy( final ElementDifference elementDifference ) {
		ElementDifference result = elementDifference;
		while ( result.childDifferences.size() == 1 && result.identifyingAttributesDifference == null
				&& result.attributesDifference == null ) {
			result = result.childDifferences.iterator().next();
		}
		return result;
	}

	@Override
	public int compareTo( final ElementDifference other ) {
		if ( getIdentifyingAttributes() == null || other.getIdentifyingAttributes() == null ) {
			throw new IllegalStateException( "Identifying attributes may not be null. Loaded leighweight XML?" );
		}
		return getIdentifyingAttributes().compareTo( other.getIdentifyingAttributes() );
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

	public Screenshot getExpectedScreenshot() {
		return expectedScreenshot;
	}

	public Screenshot getActualScreenshot() {
		return actualScreenshot;
	}

	public List<AttributeDifference> getAttributeDifferences() {
		final List<AttributeDifference> differences = new ArrayList<>();
		if ( identifyingAttributesDifference instanceof IdentifyingAttributesDifference ) {
			differences.addAll( ((IdentifyingAttributesDifference) identifyingAttributesDifference).getAttributes() );
		}
		if ( attributesDifference != null ) {
			differences.addAll( attributesDifference.getAttributes() );
		}
		return differences;
	}

	public AttributesDifference getAttributesDifference() {
		return attributesDifference;
	}

	public boolean isInsertionOrDeletion() {
		return identifyingAttributesDifference instanceof InsertedDeletedElementDifference;
	}

	public boolean isInsertion() {
		return isInsertionOrDeletion()
				&& ((InsertedDeletedElementDifference) identifyingAttributesDifference).isInserted();
	}

	public boolean isDeletion() {
		return isInsertionOrDeletion()
				&& !((InsertedDeletedElementDifference) identifyingAttributesDifference).isInserted();
	}

	public LeafDifference getIdentifyingAttributesDifference() {
		return identifyingAttributesDifference;
	}

	public String getIdentifier() {
		String result = identifyingAttributes.identifier();
		if ( identifyingAttributesDifference != null ) {
			result += getSumIdentifier( identifyingAttributesDifference.getNonEmptyDifferences() );
		}
		if ( attributesDifference != null ) {
			result += attributesDifference.getIdentifier();
		}
		return ChecksumCalculator.getInstance().sha256( result );
	}

	public static String getSumIdentifier( final Collection<ElementDifference> differences ) {
		String result = "";
		for ( final ElementDifference difference : differences ) {
			result += " # " + difference.getIdentifier();
		}
		return ChecksumCalculator.getInstance().sha256( result );
	}

	public Screenshot mark( final Screenshot screenshot ) {
		if ( screenshot == null ) {
			return null;
		}
		final List<Rectangle> marks = new ArrayList<>();
		if ( childDifferences != null ) {
			for ( final Difference childDifference : childDifferences ) {
				for ( final ElementDifference compDiff : childDifference.getNonEmptyDifferences() ) {
					marks.add( compDiff.getIdentifyingAttributes().getOutlineRectangle() );
				}
			}
		}
		return image2Screenshot( screenshot.getPersistenceIdPrefix(),
				ImageUtils.mark( screenshot2Image( screenshot ), marks ) );
	}

	public boolean hasIdentAttributesDifferences() {
		return identifyingAttributesDifference instanceof IdentifyingAttributesDifference;
	}

	public boolean hasAttributesDifferences() {
		return attributesDifference != null;
	}

	public String getElementRetestId() {
		return retestId;
	}
}
