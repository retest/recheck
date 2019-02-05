package de.retest.recheck.printer;

import java.util.function.Function;
import java.util.stream.Collectors;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;

public class TestReplayResultPrinter implements Printer<TestReplayResult> {

	private final Function<String, DefaultValueFinder> defaultValueFinder;

	public TestReplayResultPrinter( final Function<String, DefaultValueFinder> defaultValueFinder ) {
		this.defaultValueFinder = defaultValueFinder;
	}

	@Override
	public String toString( final TestReplayResult difference, final String indent ) {
		final String status = indent + testResult( difference );
		final String differences = difference.getActionReplayResults().stream() //
				.map( a -> formatAction( a, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
		return status + "\n" + differences;
	}

	private String testResult( final TestReplayResult result ) {
		final String name = result.getName();
		final int differences = result.getDifferencesCount();
		final int unique = result.getUniqueDifferences().size();
		return String.format( "Test '%s' has %d differences (%d unique):", name, differences, unique );
	}

	private String formatAction( final ActionReplayResult result, final String indent ) {
		final DefaultValueFinder finder = defaultValueFinder.apply( result.getDescription() );
		final ActionReplayResultPrinter printer = new ActionReplayResultPrinter( finder );
		return printer.toString( result, indent );
	}
}
