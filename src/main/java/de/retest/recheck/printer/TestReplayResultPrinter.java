package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;

public class TestReplayResultPrinter implements Printer<TestReplayResult> {

	private final DefaultValueFinderProvider provider;
	private final ShouldIgnore ignore;

	public TestReplayResultPrinter( final DefaultValueFinderProvider provider, final ShouldIgnore ignore ) {
		this.provider = provider;
		this.ignore = ignore;
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
		final int differences = result.getDifferences( ignore ).size();
		return String.format( "Test '%s' has %d differences in %d states:", name, differences,
				result.getActionReplayResults().size() );
	}

	private String formatAction( final ActionReplayResult result, final String indent ) {
		final DefaultValueFinder finder = provider.findForAction( result.getDescription() );
		final ActionReplayResultPrinter printer = new ActionReplayResultPrinter( finder, ignore );
		return printer.toString( result, indent );
	}
}
