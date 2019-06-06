package de.retest.recheck;

import java.util.ArrayList;

import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.suite.ExecutableSuite;
import de.retest.recheck.ui.descriptors.GroundState;

public class SuiteReplayResultProvider {

	private static SuiteReplayResultProvider instance;

	public static SuiteReplayResultProvider getInstance() {
		if ( instance == null ) {
			instance = new SuiteReplayResultProvider();
		}
		return instance;
	}

	static SuiteReplayResultProvider getTestInstance() {
		return new SuiteReplayResultProvider();
	}

	private SuiteReplayResultProvider() {}

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
		return new SuiteReplayResult( suiteName, 0, groundState, execSuite.getUuid(), groundState );
	}
}
