package de.retest.recheck.printer.highlighting;

public class DefaultHighlighter implements Highlighter {

	@Override
	public String highlight( final String msg, final HighlightType element ) {
		// DefaultHighlighter does not apply any highlighting to the (string) printers
		return msg;
	}
}
