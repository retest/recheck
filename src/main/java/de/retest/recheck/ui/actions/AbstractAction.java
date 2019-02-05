package de.retest.recheck.ui.actions;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.tuple.ImmutablePair;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.image.Screenshot;

@XmlAccessorType( XmlAccessType.FIELD )
public abstract class AbstractAction implements Action {

	private static final long serialVersionUID = 2L;

	public static final String ACTION_PREFIX = "action";

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( AbstractAction.class );

	@XmlElement
	protected final Element element;

	@XmlAttribute
	private final String uuid;

	@XmlElement
	private Screenshot[] windowsScreenshots;

	public AbstractAction( final Element element, final Screenshot[] windowScreenshots ) {
		if ( element == null ) {
			throw new NullPointerException( "Target must not be null!" );
		}
		this.element = element;
		windowsScreenshots = windowScreenshots;
		uuid = UUID.randomUUID().toString();
	}

	// This is for JAXB
	protected AbstractAction() {
		element = null;
		uuid = null;
	}

	@Override
	public int compareTo( final Action other ) {
		return getActionIdentifyingAttributes().compareTo( other.getActionIdentifyingAttributes() );
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public abstract String createDescription();

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( object != null && object instanceof AbstractAction ) {
			final AbstractAction other = (AbstractAction) object;
			if ( getActionIdentifyingAttributes().equals( other.getActionIdentifyingAttributes() ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> ActionExecutionResult execute( final Environment<T> environment ) {
		try {
			final ImmutablePair<TargetNotFoundException, Component<T>> result = environment.findTargetComponent( this );
			if ( result.left != null ) {
				return new ActionExecutionResult( result.left );
			}
			if ( getTargetElement().getScreenshot() == null ) {
				getTargetElement().setScreenshot( result.right.createScreenshot() );
			}
			execute( result.right );
			return new ActionExecutionResult( null, -1 );
		} catch ( final TargetNotFoundException exc ) {
			return new ActionExecutionResult( null, exc, -1 );
		} catch ( final Throwable t ) {
			return new ActionExecutionResult( new ExceptionWrapper( t ), -1 );
		}
	}

	protected void throwTargetNotFoundForExecution( final Component<?> component ) throws TargetNotFoundException {
		throw new TargetNotFoundException( this, component.getElement(), getWindowsScreenshots(),
				"Tried to execute a action with wrong component peer" );
	}

	@Override
	public ActionIdentifyingAttributes getActionIdentifyingAttributes() {
		// We cannot show e.g. which text is entered here!
		// Problem: We enter random text in a text field
		// This gives us a new state in which
		// entering random text in the text field is unexplored
		// Thus we do it again (loop forever)
		final ActionIdentifyingAttributes result =
				new ActionIdentifyingAttributes( element.getIdentifyingAttributes(), getClass().getName() );
		return result;
	}

	@Override
	public Element getTargetElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return getActionIdentifyingAttributes().hashCode();
	}

	@Override
	public Action randomize() {
		logger.warn( "Randomize not implemented for {}.", this.getClass() );
		return this;
	}

	@Override
	public String toString() {
		return createDescription();
	}

	@Override
	public Screenshot[] getWindowsScreenshots() {
		return windowsScreenshots;
	}

	@Override
	public void setWindowsScreenshots( final Screenshot[] windowsScreenshots ) {
		this.windowsScreenshots = windowsScreenshots;
	}
}
