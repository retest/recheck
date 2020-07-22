package de.retest.recheck.printer;

import java.util.List;
import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.HighlightType;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.diff.ElementIdentificationWarning;

public class AttributeDifferencePrinter implements Printer<AttributeDifference> {

	private static final String IS_DEFAULT = "(derived or unspecified)";

	private static final String WARNING_FILENAME_LINE_FORMAT = ", breaks=\"%s\"";

	private final IdentifyingAttributes attributes;
	private final DefaultValueFinder defaultProvider;
	private final Highlighter highlighter;

	public AttributeDifferencePrinter( final IdentifyingAttributes attributes,
			final DefaultValueFinder defaultProvider ) {
		this( attributes, defaultProvider, new DefaultHighlighter() );
	}

	public AttributeDifferencePrinter( final IdentifyingAttributes attributes, final DefaultValueFinder defaultProvider,
			final Highlighter highlighter ) {
		this.attributes = attributes;
		this.defaultProvider = defaultProvider;
		this.highlighter = highlighter;
	}

	@Override
	public String toString( final AttributeDifference difference, final String indent ) {
		if ( difference.hasElementIdentificationWarning() ) {
			return indent + format( difference ) + printWarning( difference );
		}
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
		return defaultProvider.isDefaultValue( attributes, difference.getKey(), difference.getActual() );
	}

	private String printActualDefaultDifference( final AttributeDifference difference ) {
		final String key = highlighter.highlight( difference.getKey(), HighlightType.KEY );
		final String expected = highlighter.highlight( difference.getExpectedToString(), HighlightType.VALUE_EXPECTED );
		final String keyExpectedActualFormat = getKeyExpectedActualFormat();

		return String.format( keyExpectedActualFormat, key, expected, IS_DEFAULT );
	}

	private boolean isExpectedDefault( final AttributeDifference difference ) {
		return difference.getExpected() == null; // We do not save defaults, thus this is null
	}

	private String printExpectedDefaultDifference( final AttributeDifference difference ) {
		final String key = highlighter.highlight( difference.getKey(), HighlightType.KEY );
		final String actual = highlighter.highlight( difference.getActualToString(), HighlightType.VALUE_ACTUAL );
		final String keyExpectedActualFormat = getKeyExpectedActualFormat();

		return String.format( keyExpectedActualFormat, key, IS_DEFAULT, actual );
	}

	private String printBothDifferences( final AttributeDifference difference ) {
		final String key = highlighter.highlight( difference.getKey(), HighlightType.KEY );
		final String expected = highlighter.highlight( difference.getExpectedToString(), HighlightType.VALUE_EXPECTED );
		final String actual = highlighter.highlight( difference.getActualToString(), HighlightType.VALUE_ACTUAL );
		final String keyExpectedActualFormat = getKeyExpectedActualFormat();

		return String.format( keyExpectedActualFormat, key, expected, actual );
	}

	private String printWarning( final AttributeDifference difference ) {
		final List<ElementIdentificationWarning> warnings = difference.getElementIdentificationWarnings();
		return String.format( WARNING_FILENAME_LINE_FORMAT, warnings.stream() //
				.map( this::formatWarning ) //
				.collect( Collectors.joining( ", " ) ) );
	}

	private String formatWarning( final ElementIdentificationWarning warning ) {
		return warning.getTestFileName() + ":" + warning.getTestLineNumber();
	}

	private String getKeyExpectedActualFormat() {
		final String expected = highlighter.highlight( "expected", HighlightType.EXPECTED );
		final String actual = highlighter.highlight( "actual", HighlightType.ACTUAL );
		return "%s: " + expected + "=\"%s\", " + actual + "=\"%s\"";
	}

}
