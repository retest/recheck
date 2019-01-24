package de.retest.gui.surili;

import java.io.Serializable;

public interface SimpleSearchListener extends Serializable {

	public class FitnessData implements Serializable {

		private static final long serialVersionUID = 1L;

		final int totalBranches;
		final int maxCoveredBranches;

		final int totalMethods;
		final int maxCoveredMethods;

		final int totalLines;
		final int maxCoveredLines;

		public FitnessData( final int totalBranches, final int maxCoveredBranches, final int totalMethods,
				final int maxCoveredMethods, final int totalLines, final int maxCoveredLines ) {
			this.totalBranches = totalBranches;
			this.maxCoveredBranches = maxCoveredBranches;
			this.totalMethods = totalMethods;
			this.maxCoveredMethods = maxCoveredMethods;
			this.totalLines = totalLines;
			this.maxCoveredLines = maxCoveredLines;
		}

	}

	public void searchStarted( SimpleSearchListener.FitnessData data );

	public void iteration( int populationSize );

	public void fitnessEvaluation( SimpleSearchListener.FitnessData data );

}
