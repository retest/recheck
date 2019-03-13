package de.retest.recheck.ui.diff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.retest.recheck.ignore.GloballyIgnoredAttributes;
import de.retest.recheck.image.ImageDiffCalcFactory;
import de.retest.recheck.image.ImageDifference;
import de.retest.recheck.image.ImageDifferenceCalculator;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.ScreenshotAttributeDifference;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.util.ListMap;

public class AttributesDifferenceFinder {

	private final DefaultValueFinder defaultValueFinder;
	private static final ImageDifferenceCalculator imgDiffCalc =
			ImageDiffCalcFactory.getConfiguredImageDifferenceCalculator();

	public AttributesDifferenceFinder( final DefaultValueFinder defaultValueFinder ) {
		this.defaultValueFinder = defaultValueFinder;
	}

	public AttributesDifference differenceFor( final Element expected, final Element actual ) {
		final IdentifyingAttributes identAttributes = expected.getIdentifyingAttributes();
		final Attributes expectedCrit = expected.getAttributes();
		final Attributes actualCrit = actual.getAttributes();
		final List<AttributeDifference> differences = new ArrayList<>();
		final Map<String, Serializable> unshownRightMap = new ListMap<>( actualCrit.getMap() );
		for ( final Map.Entry<String, ? extends Serializable> leftEntry : expectedCrit.getMap().entrySet() ) {
			final AttributeDifference attributeDifference =
					differenceFor( identAttributes, (Serializable) leftEntry.getValue(),
							(Serializable) actualCrit.get( leftEntry.getKey() ), leftEntry.getKey() );
			if ( attributeDifference != null ) {
				differences.add( attributeDifference );
			}
			unshownRightMap.remove( leftEntry.getKey() );
		}
		for ( final Map.Entry<String, Serializable> rightEntry : unshownRightMap.entrySet() ) {
			final AttributeDifference optionalDifference =
					differenceFor( identAttributes, (Serializable) expectedCrit.get( rightEntry.getKey() ),
							rightEntry.getValue(), rightEntry.getKey() );
			if ( optionalDifference != null ) {
				differences.add( optionalDifference );
			}
		}
		return differences.isEmpty() ? null : new AttributesDifference( differences );
	}

	private AttributeDifference differenceFor( final IdentifyingAttributes identAttributes, final Serializable expected,
			final Serializable actual, final String key ) {
		if ( GloballyIgnoredAttributes.getInstance().shouldIgnoreAttribute( key ) ) {
			return null;
		}
		if ( Objects.equals( expected, actual ) ) {
			return null;
		}
		if ( defaultValueFinder.isDefaultValue( identAttributes, key, actual ) ) {
			return null;
		}
		if ( key.equals( Attributes.SCREENSHOT ) ) {
			final Screenshot expectedScreenshot = (Screenshot) expected;
			final Screenshot actualScreenshot = (Screenshot) actual;
			final ImageDifference imgDiff = imgDiffCalc.compare( expectedScreenshot, actualScreenshot );
			if ( imgDiff.isEqual() ) {
				return null;
			} else {
				return new ScreenshotAttributeDifference( expectedScreenshot, actualScreenshot,
						imgDiff.getStrategyName(), imgDiff.getMatch() );
			}
		}
		return new AttributeDifference( key, expected, actual );
	}

}
