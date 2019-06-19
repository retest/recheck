package de.retest.recheck.printer;

import java.util.Map;
import java.util.stream.Collectors;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.SuiteReplayResult;

public class SuiteReplayResultPrinter implements Printer<SuiteReplayResult> {

	private final TestReplayResultPrinter delegate;

	public SuiteReplayResultPrinter( final DefaultValueFinderProvider provider,
			final Map<ActionReplayResult, Filter> filterByResult ) {
		delegate = new TestReplayResultPrinter( provider, filterByResult );
	}

	@Override
	public String toString( final SuiteReplayResult difference, final String indent ) {
		return difference.getTestReplayResults().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
