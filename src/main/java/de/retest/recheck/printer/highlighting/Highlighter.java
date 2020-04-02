package de.retest.recheck.printer.highlighting;

@FunctionalInterface
public interface Highlighter {
	String highlight( String msg, HighlightType element );
}
