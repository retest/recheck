package de.retest.recheck.ui.review;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.retest.recheck.ui.diff.AttributeDifference;

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

	public List<ActionChangeSet> getAllActionChangeSets() {
		return getSuiteChangeSets().stream() //
				.flatMap( suiteChangeSet -> suiteChangeSet.getTestChangeSets().stream() ) //
				.flatMap( testChangeSet -> testChangeSet.getAllActionChangeSets().stream() ) //
				.collect( Collectors.toList() );
	}

	public List<AttributeDifference> getAttributeDifferences() {
		return getAllActionChangeSets().stream()
				.flatMap( actionChangeSet -> actionChangeSet.getAllAttributeChanges().stream()
						.flatMap( attributeChanges -> attributeChanges.getChanges().values().parallelStream()
								.flatMap( attributeDifferences -> attributeDifferences.stream() ) ) )
				.collect( Collectors.toList() );
	}

	@Override
	public String toString() {
		return "ReviewResult [suiteChangeSets=" + suiteChangeSets.size() + "]";
	}

}
