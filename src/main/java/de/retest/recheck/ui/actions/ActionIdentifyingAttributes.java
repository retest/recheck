package de.retest.recheck.ui.actions;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class ActionIdentifyingAttributes implements Comparable<ActionIdentifyingAttributes>, Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private final IdentifyingAttributes identifyingAttributes;

	@XmlElement
	private final String action;

	@SuppressWarnings( "unused" )
	private ActionIdentifyingAttributes() {
		// for JAXB
		action = null;
		identifyingAttributes = null;
	}

	public ActionIdentifyingAttributes( final IdentifyingAttributes targetIdentifyingAttributes, final String action ) {
		identifyingAttributes = targetIdentifyingAttributes;
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public IdentifyingAttributes getIdentifyingAttributes() {
		return identifyingAttributes;
	}

	@Override
	public int compareTo( final ActionIdentifyingAttributes other ) {
		final int result = identifyingAttributes.compareTo( other.identifyingAttributes );
		if ( result != 0 ) {
			return result;
		}
		return action.compareTo( other.action );
	}

	@Override
	public int hashCode() {
		return identifyingAttributes.hashCode() ^ 31 + action.hashCode();
	}

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( object instanceof ActionIdentifyingAttributes ) {
			final ActionIdentifyingAttributes other = (ActionIdentifyingAttributes) object;
			if ( identifyingAttributes.getParentPath().equals( other.identifyingAttributes.getParentPath() )
					&& identifyingAttributes.getType().equals( other.identifyingAttributes.getType() )
					&& action.equals( other.action ) ) {
				return true;
			}
		}
		return false;
	}
}
