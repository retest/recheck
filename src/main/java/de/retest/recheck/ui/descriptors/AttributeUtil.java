package de.retest.recheck.ui.descriptors;

import java.awt.Rectangle;
import java.util.Objects;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;

public class AttributeUtil {

	public static Rectangle getActualOutline( final ElementDifference difference ) {
		final IdentifyingAttributesDifference identifyingAttributesDifference =
				(IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference();
		try {
			if ( Objects.nonNull( identifyingAttributesDifference.getAttributeDifferences() ) ) {
				for ( final AttributeDifference aDiff : identifyingAttributesDifference.getAttributeDifferences() ) {
					if ( aDiff.getKey().equals( OutlineAttribute.RELATIVE_OUTLINE ) ) {
						return ((Rectangle) aDiff.getActual());
					}
				}
			}
		} catch ( final Exception ignored ) {}
		return getOutline( difference.getIdentifyingAttributes() );
	}

	public static Rectangle getActualAbsoluteOutline( final ElementDifference difference ) {
		final IdentifyingAttributesDifference identifyingAttributesDifference =
				(IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference();
		try {
			if ( Objects.nonNull( identifyingAttributesDifference.getAttributeDifferences() ) ) {
				for ( final AttributeDifference aDiff : identifyingAttributesDifference.getAttributeDifferences() ) {
					if ( aDiff.getKey().equals( OutlineAttribute.ABSOLUTE_OUTLINE ) ) {
						return ((Rectangle) aDiff.getActual());
					}
				}
			}
		} catch ( final Exception ignored ) {}
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
