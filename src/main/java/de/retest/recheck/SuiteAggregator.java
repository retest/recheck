package de.retest.recheck;

import java.util.ArrayList;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReport;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.descriptors.GroundState;

public class SuiteAggregator {

	private static SuiteAggregator instance;

	public static SuiteAggregator getInstance() {
		if ( instance == null ) {
			instance = new SuiteAggregator();
		}
		return instance;
	}

	static SuiteAggregator getTestInstance() {
		return new SuiteAggregator();
	}

	private SuiteAggregator() {}

	private final TestReport aggregatedTestReport = TestReport.fromApi();

	private SuiteReplayResult currentSuite;

	public TestReport getAggregatedTestReport() {
		return aggregatedTestReport;
	}

	public SuiteReplayResult getSuite( final String suiteName ) {
		if ( currentSuite == null ) {
			currentSuite = createSuiteReplayResult( suiteName );
		}
		if ( !suiteName.equals( currentSuite.getName() ) ) {
			currentSuite = createSuiteReplayResult( suiteName );
		}
		return currentSuite;
	}

	private SuiteReplayResult createSuiteReplayResult( final String suiteName ) {
		final GroundState groundState = new GroundState();
		final ExecutableSuite execSuite = new ExecutableSuite( groundState, 0, new ArrayList<>() );
		execSuite.setName( suiteName );
		final SuiteReplayResult suiteReplayResult =
				new SuiteReplayResult( suiteName, 0, groundState, execSuite.getUuid(), groundState );
		aggregatedTestReport.addSuite( suiteReplayResult );
		return suiteReplayResult;
	}
}
