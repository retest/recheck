package de.retest.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.persistence.Persistable;
import de.retest.ui.descriptors.GroundState;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ActionSequence extends Persistable implements ActionList {

	private static final long serialVersionUID = 1L;
	private static final int PERSISTENCE_VERSION = 21;

	@XmlAnyElement( lax = true )
	private final List<Action> actions;

	@XmlElement
	private final String testName;

	@XmlElement
	private final GroundState groundState;

	@SuppressWarnings( "unused" ) // only for JAXB
	private ActionSequence() {
		this( "", null, new ArrayList<Action>() );
	}

	public ActionSequence( final String testName, final GroundState groundState, final List<Action> actions ) {
		super( PERSISTENCE_VERSION );
		this.testName = testName;
		this.groundState = groundState;
		this.actions = actions;
	}

	public String getTestname() {
		return testName;
	}

	@Override
	public void addAction( final Action action, final EventObject eo ) {
		actions.add( action );
	}

	public int size() {
		return actions.size();
	}

	public GroundState getGroundState() {
		return groundState;
	}

	public List<Action> getActions() {
		return Collections.unmodifiableList( actions );
	}

	@Override
	public String toString() {
		return testName;
	}

	@Override
	public Action getLastAction() {
		if ( actions.isEmpty() ) {
			return null;
		}
		return actions.get( actions.size() - 1 );
	}

}
