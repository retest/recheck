package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.TestReport;

public class TestReportPrinter implements Printer<TestReport> {

	private final SuiteReplayResultPrinter delegate;

	public TestReportPrinter( final DefaultValueFinderProvider provider, final Filter filter ) {
		delegate = new SuiteReplayResultPrinter( provider, filter );
	}

	@Override
	public String toString( final TestReport testReport, final String indent ) {
		return testReport.getSuiteReplayResults().stream() //
				.map( suiteReplayResult -> delegate.toString( suiteReplayResult, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

}
