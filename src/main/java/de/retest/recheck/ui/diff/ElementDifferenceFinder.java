package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.descriptors.ElementUtil.flattenChildElements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.retest.ui.DefaultValueFinder;
import de.retest.ui.descriptors.Element;
import de.retest.recheck.elementcollection.RecheckIgnore;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.Element;

public class ElementDifferenceFinder {

	private final IdentifyingAttributesDifferenceFinder identAttrDiffFinder;
	private final AttributesDifferenceFinder attributesDifferenceFinder;

	public ElementDifferenceFinder( final DefaultValueFinder defaultValueFinder ) {
		identAttrDiffFinder = new IdentifyingAttributesDifferenceFinder();
		attributesDifferenceFinder = new AttributesDifferenceFinder( defaultValueFinder );
	}

	// TODO We can have more performance optimization: a cell can only life in a row, a row only in a table etc.
	public Collection<ElementDifference> findChildDifferences( final Element expectedComponent,
			final Element actualComponent ) {
		final Alignment alignment = Alignment.createAlignment( expectedComponent, actualComponent );
		// Recreate original structure for difference, so we can skip if there are too many child differences per comp.
		final List<Element> remainingActual = new ArrayList<>( flattenChildElements( actualComponent ) );
		final Collection<ElementDifference> result =
				createHierarchicalStructure( expectedComponent.getContainedElements(), remainingActual, alignment );
		// Add components in actual that are missing in expected.
		for ( final Element element : remainingActual ) {
			final ElementDifference difference = differenceFor( null, element, remainingActual, alignment );
			if ( difference != null ) {
				result.add( difference );
			}
		}
		return result;
	}

	private Collection<ElementDifference> createHierarchicalStructure( final List<Element> expected,
			final List<Element> remainingActual, final Alignment alignment ) {
		final Collection<ElementDifference> result = new ArrayList<>();
		for ( final Element childComp : expected ) {
			final Element actualChild = alignment.get( childComp );
			final ElementDifference difference = differenceFor( childComp, actualChild, remainingActual, alignment );
			if ( difference != null ) {
				result.add( difference );
			}
			remainingActual.remove( actualChild );
		}
		return result;
	}

	public ElementDifference differenceFor( final Element expected, final Element actual,
			final List<Element> remainingActual, final Alignment alignment ) {
		AttributesDifference attributesDifference = null;
		LeafDifference identifyingAttributesDifference = null;
		final Collection<ElementDifference> childDifferences = new ArrayList<>();

		if ( expected == null ) {
			identifyingAttributesDifference = InsertedDeletedElementDifference.differenceFor( null, actual );
		} else {
			if ( actual == null ) {
				identifyingAttributesDifference = InsertedDeletedElementDifference.differenceFor( expected, null );
			} else {
				identifyingAttributesDifference = identAttrDiffFinder
						.differenceFor( expected.getIdentifyingAttributes(), actual.getIdentifyingAttributes() );
				attributesDifference = attributesDifferenceFinder.differenceFor( expected, actual );
			}
			childDifferences.addAll(
					createHierarchicalStructure( expected.getContainedElements(), remainingActual, alignment ) );
		}
		if ( identifyingAttributesDifference == null && attributesDifference == null && childDifferences.isEmpty() ) {
			return null;
		}
		return new ElementDifference( expected == null ? actual : expected, attributesDifference,
				identifyingAttributesDifference, expected == null ? null : expected.getScreenshot(),
				actual == null ? null : actual.getScreenshot(), childDifferences );
	}

	public ElementDifference differenceFor( final Element expected, final Element actual ) {
		AttributesDifference attributesDifference = null;
		LeafDifference identifyingAttributesDifference = null;
		final Collection<ElementDifference> childDifferences = new ArrayList<>();

		if ( expected == null ) {
			identifyingAttributesDifference = InsertedDeletedElementDifference.differenceFor( null, actual );
		} else {
			if ( actual == null ) {
				identifyingAttributesDifference = InsertedDeletedElementDifference.differenceFor( expected, null );
			} else {
				identifyingAttributesDifference = identAttrDiffFinder
						.differenceFor( expected.getIdentifyingAttributes(), actual.getIdentifyingAttributes() );
				attributesDifference = attributesDifferenceFinder.differenceFor( expected, actual );
				childDifferences.addAll( findChildDifferences( expected, actual ) );
			}
		}
		if ( identifyingAttributesDifference == null && attributesDifference == null && childDifferences.isEmpty() ) {
			return null;
		}
		return new ElementDifference( expected == null ? actual : expected, attributesDifference,
				identifyingAttributesDifference, expected == null ? null : expected.getScreenshot(),
				actual == null ? null : actual.getScreenshot(), childDifferences );
	}

	public static List<ElementDifference> getNonEmptyDifferences( final List<? extends Difference> differences ) {
		final List<ElementDifference> result = new ArrayList<>();
		if ( differences != null ) {
			for ( final Difference difference : differences ) {
				result.addAll( difference.getNonEmptyDifferences() );
			}
		}
		return result;
	}

	public static List<ElementDifference> getElementDifferences( final List<? extends Difference> differences ) {
		final List<ElementDifference> result = new ArrayList<>();
		for ( final Difference difference : differences ) {
			result.addAll( difference.getElementDifferences() );
		}
		return result;
	}

}
