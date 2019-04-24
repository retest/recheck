package de.retest.recheck.ui.descriptors;

import java.awt.Rectangle;
import java.util.List;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;

public class AttributeUtil {

	public static Rectangle getActualOutline( final ElementDifference difference ) {
		final Rectangle actualOutline = getActualOutline( difference, OutlineAttribute.RELATIVE_OUTLINE );

		if ( actualOutline != null ) {
			return actualOutline;
		}
		return getOutline( difference.getIdentifyingAttributes() );
	}

	public static Rectangle getActualAbsoluteOutline( final ElementDifference difference ) {
		final Rectangle actualOutline = getActualOutline( difference, OutlineAttribute.ABSOLUTE_OUTLINE );

		if ( actualOutline != null ) {
			return actualOutline;
		}
		return getAbsoluteOutline( difference.getIdentifyingAttributes() );
	}

	public static Rectangle getOutline( final IdentifyingAttributes attributes ) {
		final OutlineAttribute outlineAttribute =
				(OutlineAttribute) attributes.getAttribute( OutlineAttribute.RELATIVE_OUTLINE );
		if ( outlineAttribute == null ) {
			return null;
		}
		return outlineAttribute.getValue();
	}

	public static Rectangle getAbsoluteOutline( final IdentifyingAttributes attributes ) {
		final OutlineAttribute outlineAttribute =
				(OutlineAttribute) attributes.getAttribute( OutlineAttribute.ABSOLUTE_OUTLINE );
		if ( outlineAttribute == null ) {
			return null;
		}
		return outlineAttribute.getValue();
	}

	private static Rectangle getActualOutline( final ElementDifference difference, final String type ) {
		final List<AttributeDifference> attributeDifferences =
				((IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference())
						.getAttributeDifferences();

		if ( attributeDifferences != null ) {
			for ( final AttributeDifference aDiff : attributeDifferences ) {
				if ( aDiff.getKey().equals( type ) ) {
					return ((Rectangle) aDiff.getActual());
				}
			}
		}
		return null;
	}

}
