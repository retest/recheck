package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.report.SuiteReplayResult;

public class SuiteReplayResultPrinter implements Printer<SuiteReplayResult> {

	private final TestReplayResultPrinter delegate;

	public SuiteReplayResultPrinter( final DefaultValueFinderProvider provider ) {
		delegate = new TestReplayResultPrinter( provider );
	}

	@Override
	public String toString( final SuiteReplayResult difference, final String indent ) {
		return difference.getTestReplayResults().stream() //
				.filter( testReplayResult -> !testReplayResult.isEmpty() ) //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

}
