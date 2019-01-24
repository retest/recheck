package de.retest.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.retest.ui.descriptors.Element;

public interface SelectionController extends Remote, Serializable {

	void selectionActive( final boolean selection ) throws RemoteException;

	void unselect( final Element element ) throws RemoteException;

}
