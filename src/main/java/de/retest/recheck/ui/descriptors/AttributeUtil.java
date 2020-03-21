package de.retest.recheck.ui.descriptors;

import java.awt.Rectangle;
import java.util.List;

import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeUtil {

	private AttributeUtil() {}

	public static Rectangle getActualOutline( final ElementDifference difference ) {
		final Rectangle actualRelative = getActualOutline( difference, OutlineAttribute.RELATIVE_OUTLINE );

		if ( actualRelative != null ) {
			return actualRelative;
		}
		return getOutline( difference.getIdentifyingAttributes() );
	}

	public static Rectangle getActualAbsoluteOutline( final ElementDifference difference ) {
		final Rectangle actualAbsolute = getActualOutline( difference, OutlineAttribute.ABSOLUTE_OUTLINE );

		if ( actualAbsolute != null ) {
			return actualAbsolute;
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

	public static Rectangle getAbsoluteOutline( final ElementDifference difference ) {
		return getAbsoluteOutline( difference.getIdentifyingAttributes() );
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
		if (!(difference.getIdentifyingAttributesDifference() instanceof IdentifyingAttributesDifference)) {
			log.error( "getIdentifyingAttributesDifference should return an IdentifyingAttributesDifference!" );
			return difference.getElement().getIdentifyingAttributes().getOutlineRectangle();
		}
		final IdentifyingAttributesDifference identifyingAttributesDifference =
				(IdentifyingAttributesDifference) difference.getIdentifyingAttributesDifference();

		if ( identifyingAttributesDifference != null ) {

			final List<AttributeDifference> attributeDifferences =
					identifyingAttributesDifference.getAttributeDifferences();

			if ( attributeDifferences != null ) {
				for ( final AttributeDifference aDiff : attributeDifferences ) {
					if ( aDiff.getKey().equals( type ) ) {
						return (Rectangle) aDiff.getActual();
					}
				}
			}
		}
		return null;
	}

}
