package de.retest.recheck.printer;

import java.util.function.Function;
import java.util.stream.Collectors;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;

public class ReplayResultPrinter implements Printer<ReplayResult> {

	private final SuiteReplayResultPrinter delegate;

	public ReplayResultPrinter( final Function<String, DefaultValueFinder> defaultValueFinder,
			final ShouldIgnore ignore ) {
		delegate = new SuiteReplayResultPrinter( defaultValueFinder, ignore );
	}

	@Override
	public String toString( final ReplayResult difference, final String indent ) {
		return difference.getSuiteReplayResults().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

}
