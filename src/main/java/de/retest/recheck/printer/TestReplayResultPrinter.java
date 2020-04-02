package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.HighlightType;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;

public class TestReplayResultPrinter implements Printer<TestReplayResult> {

	private final DefaultValueFinderProvider provider;
	private final Highlighter highlighter;

	public TestReplayResultPrinter( final DefaultValueFinderProvider provider ) {
		this.provider = provider;
		highlighter = new DefaultHighlighter();
	}

	public TestReplayResultPrinter( final DefaultValueFinderProvider provider, final Highlighter highlighter ) {
		this.provider = provider;
		this.highlighter = highlighter;
	}

	@Override
	public String toString( final TestReplayResult difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent );
	}

	private String createDescription( final TestReplayResult result ) {
		final String name = highlighter.highlight( result.getName(), HighlightType.TESTREPLAYRESULT_NAME );
		final int differences = result.getDifferences().size();
		final int states = result.getActionReplayResults().size();
		return String.format( "Test '%s' has %d difference(s) in %d state(s):", name, differences, states );
	}

	private String createDifferences( final TestReplayResult difference, final String indent ) {
		return difference.getActionReplayResults().stream() //
				.filter( this::shouldPrint ) //
				.map( a -> formatAction( a, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

	private boolean shouldPrint( final ActionReplayResult actionReplayResult ) {
		return actionReplayResult.hasDifferences() //
				|| !actionReplayResult.getMetadataDifference().isEmpty();
	}

	private String formatAction( final ActionReplayResult result, final String indent ) {
		final DefaultValueFinder finder = provider.findForAction( result.getDescription() );
		final ActionReplayResultPrinter printer = new ActionReplayResultPrinter( finder, highlighter );
		return printer.toString( result, indent );
	}
}
