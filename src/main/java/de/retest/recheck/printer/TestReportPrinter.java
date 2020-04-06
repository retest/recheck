package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.printer.highlighting.DefaultHighlighter;
import de.retest.recheck.printer.highlighting.Highlighter;
import de.retest.recheck.report.TestReport;

public class TestReportPrinter implements Printer<TestReport> {

	private final SuiteReplayResultPrinter delegate;

	public TestReportPrinter( final DefaultValueFinderProvider provider ) {
		this( new SuiteReplayResultPrinter( provider, new DefaultHighlighter() ) );
	}

	public TestReportPrinter( final DefaultValueFinderProvider provider, final Highlighter highlighter ) {
		this( new SuiteReplayResultPrinter( provider, highlighter ) );
	}

	public TestReportPrinter( final SuiteReplayResultPrinter delegate ) {
		this.delegate = delegate;
	}

	@Override
	public String toString( final TestReport testReport, final String indent ) {
		if ( testReport.isEmpty() ) {
			return "No differences found.";
		}
		return testReport.getSuiteReplayResults().stream() //
				.filter( suiteReplayResult -> !suiteReplayResult.isEmpty() ) //
				.map( suiteReplayResult -> delegate.toString( suiteReplayResult, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
