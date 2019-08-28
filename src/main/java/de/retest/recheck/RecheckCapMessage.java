package de.retest.recheck;

import java.io.File;
import java.util.stream.Collectors;

import de.retest.recheck.printer.TestReplayResultPrinter;
import de.retest.recheck.report.TestReplayResult;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class RecheckCapMessage {

	private final String suiteName;
	private final TestReplayResult testReplayResult;
	private final TestReplayResultPrinter testReplayResultPrinter;
	private final File resultFile;

	@Override
	public String toString() {
		return testReplayResult.hasNoGoldenMaster() ? getNoGoldenMasterErrorMessage() : getDifferencesErrorMessage();
	}

	private String getNoGoldenMasterErrorMessage() {
		final String goldenMasterPath = testReplayResult.getActionReplayResults().stream() //
				.map( actionReplayResult -> "\t" + actionReplayResult.getGoldenMasterPath() ) //
				.collect( Collectors.joining( "\n" ) );
		return "'" + suiteName + "': " + NoGoldenMasterActionReplayResult.MSG_LONG + "\n" + goldenMasterPath;
	}

	private String getDifferencesErrorMessage() {
		final int numChecks = testReplayResult.getActionReplayResults().size();
		final String allDiffs = testReplayResultPrinter.toString( testReplayResult );
		final String reportPath = resultFile.getAbsolutePath();

		return "A detailed report will be created at '" + reportPath + "'. " //
				+ "You can review the details by using our CLI (https://github.com/retest/recheck.cli/) or GUI (https://retest.de/review/).\n" //
				+ "\n" //
				+ numChecks + " check(s) in '" + suiteName + "' found the following difference(s):\n" //
				+ allDiffs;
	}
}
