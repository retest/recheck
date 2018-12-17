package de.retest.recheck.printer;

import java.io.Serializable;

import de.retest.ui.descriptors.AttributeDifference;

public class AttributeDifferencePrinter implements Printer<AttributeDifference> {

	private static final String IS_DEFAULT = "(default)";

	private static final String KEY_EXPECTED_ACTUAL_FORMAT = "%s: expected=\"%s\", actual=\"%s\"";

	private final PrinterValueProvider defaultProvider;

	public AttributeDifferencePrinter( final PrinterValueProvider defaultProvider ) {
		this.defaultProvider = defaultProvider;
	}

	@Override
	public String toString( final AttributeDifference difference, final String indent ) {
		return indent + format( difference );
	}

	private String format( final AttributeDifference difference ) {
		if ( isExpectedDefault( difference ) ) { // The attribute changed from default to a non-default
			return printExpectedDefaultDifference( difference );
		}
		if ( isActualDefault( difference ) ) { // The attribute changed back to default
			return printActualDefaultDifference( difference );
		}
		return printBothDifferences( difference );
	}

	private boolean isActualDefault( final AttributeDifference difference ) {
		return defaultProvider.isDefault( difference.getKey(), difference.getActual() );
	}

	private String printActualDefaultDifference( final AttributeDifference difference ) {
		final String key = difference.getKey();
		final Serializable expected = difference.getExpected();
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, key, expected, IS_DEFAULT );
	}

	private boolean isExpectedDefault( final AttributeDifference difference ) {
		return difference.getExpected() == null; // We do not save defaults, thus this is null
	}

	private String printExpectedDefaultDifference( final AttributeDifference difference ) {
		final String key = difference.getKey();
		final Serializable actual = difference.getActual();
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, key, IS_DEFAULT, actual );
	}

	private String printBothDifferences( final AttributeDifference difference ) {
		final String key = difference.getKey();
		final Serializable expected = difference.getExpected();
		final Serializable actual = difference.getActual();
		return String.format( KEY_EXPECTED_ACTUAL_FORMAT, key, expected, actual );
	}
}
