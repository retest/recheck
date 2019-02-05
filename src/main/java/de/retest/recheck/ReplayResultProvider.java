package de.retest.recheck;

import java.util.ArrayList;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.descriptors.GroundState;

public class ReplayResultProvider {

	private static ReplayResultProvider instance;

	public static ReplayResultProvider getInstance() {
		if ( instance == null ) {
			instance = new ReplayResultProvider();
		}
		return instance;
	}

	static ReplayResultProvider getTestInstance() {
		return new ReplayResultProvider();
	}

	private ReplayResultProvider() {}

	private SuiteReplayResult currentSuite;

	public SuiteReplayResult getSuite( final String suiteName ) {
		if ( currentSuite == null) {
			currentSuite = createSuiteReplayResult( suiteName );
		}
		if ( !suiteName.equals( currentSuite.getSuiteName() ) ) {
			currentSuite = createSuiteReplayResult( suiteName );
		}
		return currentSuite;
	}

	private SuiteReplayResult createSuiteReplayResult( final String suiteName ) {
		final GroundState groundState = new GroundState();
		final ExecutableSuite execSuite = new ExecutableSuite( groundState, 0, new ArrayList<>() );
		execSuite.setName( suiteName );
		return new SuiteReplayResult( execSuite, 0, groundState );
	}
}
