package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.HighlightType;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.ui.diff.meta.MetadataDifference;
import de.retest.recheck.ui.diff.meta.MetadataElementDifference;

public class MetadataDifferencePrinter implements Printer<MetadataDifference> {

	private final Highlighter highlighter;

	public MetadataDifferencePrinter() {
		this( new DefaultHighlighter() );
	}

	public MetadataDifferencePrinter( final Highlighter highlighter ) {
		this.highlighter = highlighter;
	}

	@Override
	public String toString( final MetadataDifference difference, final String indent ) {
		final String prefix = highlighter.highlight( "Metadata Differences:\n", HighlightType.HEADING_METADATA );
		final String note = highlighter.highlight(
				"Please note that these differences do not affect the result and are not included in the difference count.",
				HighlightType.NOTE );
		final String noteIndent = indent + "  ";
		final String differenceIndent = indent + "\t";
		return indent + prefix + noteIndent + note + printDifferences( difference, differenceIndent );
	}

	private String printDifferences( final MetadataDifference difference, final String indent ) {
		return difference.getDifferences().stream() //
				.map( d -> print( d, indent ) ) //
				.collect( Collectors.joining( "\n" + indent, "\n" + indent, "" ) );
	}

	private String print( final MetadataElementDifference difference, final String indent ) {
		final String expectedItemIndent = indent + "  ";
		final String actualItemIndent = indent + "    "; // tab leads to wrong indent on console

		final String keyExpectedActualFormat =
				"%s:\n" + expectedItemIndent + "expected=\"%s\",\n" + actualItemIndent + "actual=\"%s\"";

		return String.format( keyExpectedActualFormat, difference.getKey(), difference.getExpected(),
				difference.getActual() );
	}

}
