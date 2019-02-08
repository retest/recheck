package de.retest.recheck.ui.diff;

import java.util.List;

import de.retest.recheck.ui.descriptors.SutState;

public class DifferenceResult {

	private final List<RootElementDifference> differences;
	private final SutState currentSutState;

	public DifferenceResult( final SutState currentSutState, final List<RootElementDifference> differences ) {
		this.currentSutState = currentSutState;
		this.differences = differences;
	}

	public List<RootElementDifference> getDifferences() {
		return differences;
	}

	public SutState getCurrentSutState() {
		return currentSutState;
	}

}
