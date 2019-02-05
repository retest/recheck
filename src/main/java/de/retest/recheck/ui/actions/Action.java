package de.retest.recheck.ui.actions;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.image.Screenshot;
import de.retest.recheck.ui.review.ActionChangeSet;

public interface Action extends Serializable, Comparable<Action> {

	Action randomize();

	/**
	 * Executes this action in the given environment. Returns a {@link Throwable} if this causes an exception within the
	 * SUT.
	 *
	 * @param <T>
	 *            the enviroment type
	 *
	 * @param environment
	 *            enviroment in which the action will be executed
	 *
	 * @return the result of the execution
	 */
	<T> ActionExecutionResult execute( Environment<T> environment );

	void execute( Component<?> component ) throws TargetNotFoundException;

	Element getTargetElement();

	ActionIdentifyingAttributes getActionIdentifyingAttributes();

	@Override
	int hashCode();

	Screenshot[] getWindowsScreenshots();

	@XmlTransient
	void setWindowsScreenshots( Screenshot[] windowsScreenshots );

	String getUuid();

	Action applyChanges( ActionChangeSet reviewResult );

}
