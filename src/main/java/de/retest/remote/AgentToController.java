package de.retest.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.retest.ui.actions.Action;

interface AgentToController extends Remote, Serializable {

	void recorded( final Action action ) throws RemoteException;

	void started( final int pid, final ControllerToAgent remote ) throws RemoteException;

	void stopped( final int pid, final int exitCode ) throws RemoteException;

}
