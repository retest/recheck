package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.descriptors.IdentifyingAttributes.PATH_ATTRIBUTE_KEY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.retest.recheck.ignore.GloballyIgnoredAttributes;
import de.retest.recheck.ui.descriptors.AdditionalAttributeDifference;
import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.util.ObjectUtil;

public class IdentifyingAttributesDifferenceFinder {

	public IdentifyingAttributesDifference differenceFor( final IdentifyingAttributes expected,
			final IdentifyingAttributes actual ) {
		return differenceFor( expected, actual, GloballyIgnoredAttributes.getInstance() );
	}

	IdentifyingAttributesDifference differenceFor( final IdentifyingAttributes expected,
			final IdentifyingAttributes actual, final GloballyIgnoredAttributes ignored ) {
		Objects.requireNonNull( expected, "Expected cannot be null!" );
		Objects.requireNonNull( actual, "Actual cannot be null!" );

		final List<AttributeDifference> attributeDifferences = new ArrayList<>();

		final List<Attribute> expectedAttributes = expected.getAttributes();
		for ( final Attribute expectedAttr : expectedAttributes ) {
			final String key = expectedAttr.getKey();
			final Serializable expectedValue = expectedAttr.getValue();
			final Serializable actualValue = actual.get( key );

			if ( expectedAttr.isNotVisible() || ignored.shouldIgnoreAttribute( key ) ) {
				continue;
			}

			if ( key.equals( PATH_ATTRIBUTE_KEY ) ) {
				if ( pathDiffers( expected, actual ) ) {
					attributeDifferences.add( new AttributeDifference( key, expected.getPath(), actual.getPath() ) );
				}
			} else {
				if ( differs( expectedValue, actualValue ) ) {
					attributeDifferences.add( new AttributeDifference( key, expectedValue, actualValue ) );
				}
			}
		}

		actual.getAttributes().stream() //
				.filter( actualAttr -> expected.getAttribute( actualAttr.getKey() ) == null ) //
				.map( additionalAttr -> new AdditionalAttributeDifference( additionalAttr.getKey(), additionalAttr ) ) //
				.forEach( attributeDifferences::add );

		return attributeDifferences.isEmpty() ? null
				: new IdentifyingAttributesDifference( expected, attributeDifferences );
	}

	private static boolean pathDiffers( final IdentifyingAttributes expected, final IdentifyingAttributes actual ) {
		return !expected.getPathElement().equals( actual.getPathElement() );
	}

	// We treat null == "". This is OK for visible attributeDifferences but less suitable for invisible ones.
	private static boolean differs( final Object expected, final Object actual ) {
		return ObjectUtil.isNullOrEmptyString( expected ) ? !ObjectUtil.isNullOrEmptyString( actual )
				: !expected.equals( actual );
	}

}
