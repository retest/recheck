package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.HighlightType;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.report.SuiteReplayResult;

public class SuiteReplayResultPrinter implements Printer<SuiteReplayResult> {

	private final TestReplayResultPrinter delegate;
	private final Highlighter highlighter;

	public SuiteReplayResultPrinter( final DefaultValueFinderProvider provider ) {
		delegate = new TestReplayResultPrinter( provider );
		highlighter = new DefaultHighlighter();
	}

	public SuiteReplayResultPrinter( final DefaultValueFinderProvider provider, final Highlighter highlighter ) {
		delegate = new TestReplayResultPrinter( provider, highlighter );
		this.highlighter = highlighter;
	}

	@Override
	public String toString( final SuiteReplayResult difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent + "\t" );
	}

	private String createDescription( final SuiteReplayResult difference ) {
		final String name = difference.getName();
		final int differences = difference.getDifferencesCount();
		final int states = difference.getTestReplayResults().size();
		return String.format( highlighter.highlight( "Suite '%s' has %d difference(s) in %d test(s):",
				HighlightType.HEADING_SUITE_RESULTS ), name, differences, states );
	}

	private String createDifferences( final SuiteReplayResult difference, final String indent ) {
		return difference.getTestReplayResults().stream() //
				.filter( testReplayResult -> !testReplayResult.isEmpty() ) //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

}
