package de.retest.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.retest.ui.descriptors.Element;

public interface SelectionCallback extends Remote, Serializable {

	void select( final Element element ) throws RemoteException;

	void unselect( final Element element ) throws RemoteException;

	int getGlassPaneColor() throws RemoteException;

}
