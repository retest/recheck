package de.retest.recheck.ui.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ActionStateSequence implements Serializable {

	private static final long serialVersionUID = 3L;

	@XmlElement
	private final SutState initialState;

	@XmlElement
	private final List<ActionState> actionStates;

	@XmlElement
	private final String name;

	@XmlElement
	private final long sumDuration;

	@XmlElement
	private long startupDuration;

	@SuppressWarnings( "unused" )
	private ActionStateSequence() {
		// for JAXB
		initialState = null;
		actionStates = null;
		name = null;
		sumDuration = 0L;
	}

	public ActionStateSequence( final SutState initialState, final List<ActionState> actionStates, final String name,
			final long sumDuration, final long startupDuration ) {
		if ( initialState == null || actionStates == null ) {
			throw new NullPointerException( "initialState and actionStates cannot be null." );
		}
		this.initialState = initialState;
		this.actionStates = actionStates;
		this.name = name;
		this.sumDuration = sumDuration;
		this.startupDuration = startupDuration;
	}

	public SutState getInitialState() {
		return initialState;
	}

	public List<ActionState> getActionStates() {
		return actionStates;
	}

	public String getName() {
		return name;
	}

	public long getSumDuration() {
		return sumDuration;
	}

	public long getStartupDuration() {
		return startupDuration;
	}

	public ActionStateSequence applyChanges( final TestChangeSet testChangeSet ) {
		final SutState newInitialState = manipulateInitialState( initialState, testChangeSet );
		final List<ActionState> newActionStates = new ArrayList<>();

		// usage of index is imported here, because need a mapping change set to test.
		// Other value as index are not useful, because a test can contain an action multiple times.
		for ( int i = 0; i < actionStates.size(); i++ ) {
			newActionStates.add( manipulate( actionStates.get( i ), testChangeSet.getActionChangeSet( i ) ) );
		}

		return new ActionStateSequence( newInitialState, newActionStates, name, sumDuration, startupDuration );
	}

	private static SutState manipulateInitialState( final SutState initialState, final TestChangeSet testChangeSet ) {
		if ( testChangeSet.containsInitialStateChangeSet() ) {
			return initialState.applyChanges( testChangeSet.getInitialStateChangeSet() );
		} else {
			return initialState;
		}
	}

	private static ActionState manipulate( final ActionState actionWithUiState,
			final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet != null ) {
			return actionWithUiState.applyChanges( actionChangeSet );
		} else {
			return actionWithUiState;
		}
	}
}
