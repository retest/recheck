package de.retest.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;

import de.retest.ui.actions.Action;
import de.retest.ui.actions.ActionExecutionResult;
import de.retest.ui.descriptors.RootElement;

interface ControllerToAgent extends Remote, Serializable {

	ActionExecutionResult doAction( final Action action ) throws RemoteException;

	void waitForStabilization() throws RemoteException;

	List<RootElement> getRootElements() throws RemoteException;

	List<Action> getAllActions() throws RemoteException;

	SelectionController selectComponents( final SelectionCallback callback ) throws RemoteException;

	interface FitnessData<T> extends Serializable {
		T get();
	}

	FitnessData<?> getFitnessData() throws RemoteException;

	<T> T getGenericData( Callable<T> c ) throws RemoteException;

}
