package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.TestReport;

public class TestReportPrinter implements Printer<TestReport> {

	private final SuiteReplayResultPrinter delegate;

	public TestReportPrinter( final DefaultValueFinderProvider provider, final ShouldIgnore ignore ) {
		delegate = new SuiteReplayResultPrinter( provider, ignore );
	}

	@Override
	public String toString( final TestReport testReport, final String indent ) {
		return testReport.getSuiteReplayResults().stream() //
				.map( suiteReplayResult -> delegate.toString( suiteReplayResult, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

}
