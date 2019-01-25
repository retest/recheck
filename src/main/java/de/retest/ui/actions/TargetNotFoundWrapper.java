package de.retest.ui.actions;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.retest.ui.descriptors.Element;
import de.retest.ui.image.Screenshot;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class TargetNotFoundWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlAnyElement( lax = true )
	private final Action action;

	@XmlElement
	private final Element bestMatch;

	@XmlElement
	private final Screenshot[] windowsScreenshots;

	@XmlElement
	private final String message;

	@XmlTransient
	private TargetNotFoundException tnfe;

	@SuppressWarnings( "unused" )
	private TargetNotFoundWrapper() {
		// for JAXB
		action = null;
		bestMatch = null;
		message = "";
		windowsScreenshots = new Screenshot[] {};
	}

	public TargetNotFoundWrapper( final TargetNotFoundException tnfe ) {
		action = tnfe.getAction();
		bestMatch = tnfe.getBestMatch();
		windowsScreenshots = tnfe.getActualWindowsScreenshots();
		message = tnfe.getMessage();
		this.tnfe = tnfe;
	}

	public Element getMissingTarget() {
		return action != null ? action.getTargetElement() : null;
	}

	public Screenshot[] getExpectedWindowsScreenshots() {
		return action != null ? action.getWindowsScreenshots() : new Screenshot[0];
	}

	public Screenshot[] getActualWindowsScreenshots() {
		return windowsScreenshots;
	}

	public Element getBestMatch() {
		return bestMatch;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getTargetNotFoundException() {
		return tnfe;
	}

	@Override
	public String toString() {
		if ( action == null ) {
			return getClass().getName() + ": " + message;
		}
		return getClass().getName() + ": Action " + action.toString() + ": " + message;
	}
}
