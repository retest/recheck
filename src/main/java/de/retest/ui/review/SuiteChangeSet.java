package de.retest.ui.review;

import static de.retest.ui.review.GoldenMasterSource.RECORDED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuiteChangeSet {

	private final ArrayList<TestChangeSet> testChangeSets;
	private final String suiteName;
	private final String uuid;
	private final GoldenMasterSource source;

	SuiteChangeSet( final String suiteName, final String uuid ) {
		this( suiteName, uuid, RECORDED );
	}

	SuiteChangeSet( final String suiteName, final String uuid, final GoldenMasterSource source ) {
		this.suiteName = suiteName;
		this.uuid = uuid;
		this.source = source;
		testChangeSets = new ArrayList<>();
	}

	public TestChangeSet createTestChangeSet() {
		final TestChangeSet testChangeSet = new TestChangeSet();
		testChangeSets.add( testChangeSet );
		return testChangeSet;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public String getUuid() {
		return uuid;
	}

	/**
	 * Returns only non empty TestChangeSet
	 *
	 * @param index
	 *            the index of the TestChangeSet that should be returned
	 * @return a non empty TestChangeSet
	 */
	public TestChangeSet getTestChangeSet( final int index ) {
		final TestChangeSet testChangeSet = testChangeSets.get( index );
		if ( testChangeSet == null || testChangeSet.isEmpty() ) {
			return null;
		} else {
			return testChangeSet;
		}
	}

	public boolean isEmpty() {
		for ( final TestChangeSet testChangeSet : testChangeSets ) {
			if ( !testChangeSet.isEmpty() ) {
				return false;
			}
		}
		return true;
	}

	public GoldenMasterSource getGoldenMasterSource() {
		return source;
	}

	public List<TestChangeSet> getTestChangeSets() {
		return Collections.unmodifiableList( testChangeSets );
	}
}
