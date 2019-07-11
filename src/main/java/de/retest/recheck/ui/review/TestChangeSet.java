package de.retest.recheck.ui.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestChangeSet {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( TestChangeSet.class );

	private final ArrayList<ActionChangeSet> actionChangeSets;
	private ActionChangeSet initialStateChangeSet;

	TestChangeSet() {
		initialStateChangeSet = new ActionChangeSet();
		actionChangeSets = new ArrayList<>();
	}

	public ActionChangeSet createInitialActionChangeSet( final String description, final String goldenMasterPath ) {
		initialStateChangeSet = new ActionChangeSet( description, goldenMasterPath );
		return initialStateChangeSet;
	}

	public ActionChangeSet createActionChangeSet() {
		return createActionChangeSet( null, null );
	}

	public ActionChangeSet createActionChangeSet( final String description, final String goldenMasterPath ) {
		final ActionChangeSet actionChangeSet = new ActionChangeSet( description, goldenMasterPath );
		actionChangeSets.add( actionChangeSet );
		return actionChangeSet;
	}

	public ActionChangeSet getInitialStateChangeSet() {
		return initialStateChangeSet;
	}

	public boolean containsInitialStateChangeSet() {
		return !initialStateChangeSet.isEmpty();
	}

	/**
	 * Returns only non empty ActionChangeSet
	 *
	 * @param index
	 *            the index of the ActionChangeSet that should be returned
	 * @return a non empty ActionChangeSet
	 */
	public ActionChangeSet getActionChangeSet( final int index ) {
		assert index < actionChangeSets.size() : "The given index should be smaller than the size of actionChangeSets!";
		if ( index >= actionChangeSets.size() ) {
			// This seems to occur when changes get applied multiple times
			// if so, it should be fixed with RET-551
			logger.error( "The given index should be smaller than the size of actionChangeSets!" );
			return null;
		}
		final ActionChangeSet actionChangeSet = actionChangeSets.get( index );
		if ( actionChangeSet == null || actionChangeSet.isEmpty() ) {
			return null;
		} else {
			return actionChangeSet;
		}
	}

	public List<ActionChangeSet> getAllActionChangeSets() {
		final List<ActionChangeSet> all = new ArrayList<>();
		all.add( initialStateChangeSet );
		all.addAll( actionChangeSets );
		return all;
	}

	public boolean isEmpty() {
		return initialStateChangeSet.isEmpty() && isEmpty( actionChangeSets );
	}

	private static boolean isEmpty( final ArrayList<ActionChangeSet> actionChangeSets ) {
		for ( final ActionChangeSet actionChangeSet : actionChangeSets ) {
			if ( !actionChangeSet.isEmpty() ) {
				return false;
			}
		}
		return true;
	}

	public List<ActionChangeSet> getActionChangeSets() {
		return Collections.unmodifiableList( actionChangeSets );
	}
}
