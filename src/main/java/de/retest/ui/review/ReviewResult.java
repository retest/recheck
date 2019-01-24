package de.retest.ui.review;

import java.util.ArrayList;
import java.util.List;

public class ReviewResult {

	private final ArrayList<SuiteChangeSet> suiteChangeSets = new ArrayList<>();

	public SuiteChangeSet createSuiteChangeSet( final String suiteName, final String uuid ) {
		return createSuiteChangeSet( suiteName, uuid, GoldenMasterSource.RECORDED );
	}

	public SuiteChangeSet createSuiteChangeSet( final String suiteName, final String uuid,
			final GoldenMasterSource source ) {
		final SuiteChangeSet suiteChangeSet = new SuiteChangeSet( suiteName, uuid, source );
		suiteChangeSets.add( suiteChangeSet );
		return suiteChangeSet;
	}

	/**
	 * @return only non empty SuiteChangeSet
	 */
	public List<SuiteChangeSet> getSuiteChangeSets() {
		final ArrayList<SuiteChangeSet> nonEmpyChangeSets = new ArrayList<>();
		for ( final SuiteChangeSet suiteChangeSet : suiteChangeSets ) {
			if ( !suiteChangeSet.isEmpty() ) {
				nonEmpyChangeSets.add( suiteChangeSet );
			}
		}
		return nonEmpyChangeSets;
	}

	@Override
	public String toString() {
		return "ReviewResult [suiteChangeSets=" + suiteChangeSets.size() + "]";
	}

}
