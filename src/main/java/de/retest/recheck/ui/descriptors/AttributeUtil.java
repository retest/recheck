package de.retest.recheck.ui.descriptors;

import java.awt.Rectangle;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;

public class AttributeUtil {

	public static Rectangle getActualOutline( final ElementDifference difference ) {
		final IdentifyingAttributesDifference diff =
				(IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference();
		for ( final AttributeDifference aDiff : diff.getAttributeDifferences() ) {
			if ( aDiff.getKey().equals( OutlineAttribute.RELATIVE_OUTLINE ) ) {
				return ((Rectangle) aDiff.getActual());
			}
		}
		return getOutline( difference.getIdentifyingAttributes() );
	}

	public static Rectangle getActualAbsoluteOutline( final ElementDifference difference ) {
		final IdentifyingAttributesDifference diff =
				(IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference();
		for ( final AttributeDifference aDiff : diff.getAttributeDifferences() ) {
			if ( aDiff.getKey().equals( OutlineAttribute.ABSOLUTE_OUTLINE ) ) {
				((OutlineAttribute) aDiff.getActual()).getValue();
			}
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
}
