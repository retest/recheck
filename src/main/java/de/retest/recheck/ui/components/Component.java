package de.retest.recheck.ui.components;

import java.awt.Rectangle;
import java.util.List;

import de.retest.recheck.ui.actions.Action;
import de.retest.recheck.ui.descriptors.Attributes;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.image.Screenshot;

public interface Component<T> {

	List<Action> getPossibleActions();

	boolean isTargetable();

	/**
	 * @return Attributes to identify the current state of the component (i.e. the currently displayed text in a
	 *         textbox). These attributes are disjunct from {@link #getIdentifyingAttributes()}. In contrast to
	 *         {@link #retrieveIdentifyingAttributes()}, these attributes can be buffered.
	 */
	Attributes getAttributes();

	/**
	 * @return the state attributes from the underlying component.
	 */
	Attributes retrieveAttributes();

	/**
	 * @return Attributes to uniquely identify the component within the GUI. These attributes are disjunct from
	 *         {@link #getAttributes()}. In contrast to {@link #retrieveIdentifyingAttributes()}, these attributes can
	 *         be buffered.
	 */
	IdentifyingAttributes getIdentifyingAttributes();

	/**
	 * @return the identification attributes from the underlying component.
	 */
	IdentifyingAttributes retrieveIdentifyingAttributes();

	Element getElement();

	String getText();

	double match( IdentifyingAttributes identAttributes );

	String getPath();

	T getComponent();

	Screenshot createScreenshot();

	Rectangle getOutlineInWindowCoordinates();
}
