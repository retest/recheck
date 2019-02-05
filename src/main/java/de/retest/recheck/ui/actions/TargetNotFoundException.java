package de.retest.recheck.ui.actions;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.image.Screenshot;

public class TargetNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Action action;
	private final Element bestMatch;
	private final Screenshot[] windowsScreenshots;
	private final String message;

	/**
	 * Only for JAXB.
	 */
	@SuppressWarnings( "unused" )
	private TargetNotFoundException() {
		action = null;
		bestMatch = null;
		message = "";
		windowsScreenshots = new Screenshot[] {};
	}

	public TargetNotFoundException( final Action action, final Element bestMatch, final Screenshot[] windowsScreenshots,
			final String message ) {
		this.action = action;
		this.bestMatch = bestMatch;
		this.windowsScreenshots = windowsScreenshots;
		this.message = message;
	}

	public Action getAction() {
		return action;
	}

	public Element getBestMatch() {
		return bestMatch;
	}

	public Screenshot[] getActualWindowsScreenshots() {
		return windowsScreenshots;
	}

	public Screenshot[] getExpectedWindowsScreenshots() {
		return action.getWindowsScreenshots();
	}

	public Element getMissingTarget() {
		return action.getTargetElement();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		if ( action == null ) {
			return getClass().getName() + ": " + message;
		}
		return getClass().getName() + ": Action " + action + ": " + message;
	}

}
