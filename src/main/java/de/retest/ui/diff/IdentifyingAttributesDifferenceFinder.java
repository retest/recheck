package de.retest.ui.diff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.retest.elementcollection.RecheckIgnore;
import de.retest.ui.descriptors.AdditionalAttributeDifference;
import de.retest.ui.descriptors.Attribute;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.util.ObjectUtil;

public class IdentifyingAttributesDifferenceFinder {

	private final RecheckIgnore recheckIgnore;

	public IdentifyingAttributesDifferenceFinder() {
		this( RecheckIgnore.getInstance() );
	}

	public IdentifyingAttributesDifferenceFinder( final RecheckIgnore recheckIgnore ) {
		this.recheckIgnore = recheckIgnore;
	}

	public IdentifyingAttributesDifference differenceFor( final IdentifyingAttributes expected,
			final IdentifyingAttributes actual ) {
		Objects.requireNonNull( expected, "Expected cannot be null!" );
		Objects.requireNonNull( actual, "Actual cannot be null!" );

		final List<AttributeDifference> attributeDifferences = new ArrayList<>();

		for ( final Attribute expectedAttr : expected.getAttributes() ) {
			if ( Double.compare( expectedAttr.getWeight(), 0.0d ) == 0 || expectedAttr.isNotVisible() ) {
				continue;
			}
			final String key = expectedAttr.getKey();
			final Serializable expectedValue = expectedAttr.getValue();
			final Serializable actualValue = actual.get( key );
			if ( recheckIgnore.shouldIgnoreAttribute( expected, key ) ) {
				continue;
			}
			if ( key.equals( "path" ) ) {
				if ( pathDiffers( expected, actual ) ) {
					attributeDifferences.add( new AttributeDifference( key, expected.getPath(), actual.getPath() ) );
				}
				continue;
			}
			if ( expectedValue == null && actualValue != null ) {
				attributeDifferences.add( new AdditionalAttributeDifference( key, actual.getAttribute( key ) ) );
			} else if ( differs( expectedValue, actualValue ) ) {
				attributeDifferences.add( new AttributeDifference( key, expectedValue, actualValue ) );
			}
		}

		return attributeDifferences.isEmpty() ? null
				: new IdentifyingAttributesDifference( expected, attributeDifferences );
	}

	private static boolean pathDiffers( final IdentifyingAttributes expected, final IdentifyingAttributes actual ) {
		return !expected.getPathElement().toString().replace( expected.getParentPath(), actual.getParentPath() )
				.equals( actual.getPathElement().toString() );
	}

	// We treat null == "". This is OK for visible attributeDifferences but less suitable for invisible ones.
	private static boolean differs( final Object expected, final Object actual ) {
		return ObjectUtil.isNullOrEmptyString( expected ) ? !ObjectUtil.isNullOrEmptyString( actual )
				: !expected.equals( actual );
	}

}
