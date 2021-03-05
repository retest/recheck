package de.retest.recheck.ui.actions;

import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ui.Environment;
import de.retest.recheck.ui.components.Component;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.image.Screenshot;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

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
		return getActionIdentifyingAttributesOf( this ).compareTo( getActionIdentifyingAttributesOf( other ) );
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
		if ( object instanceof AbstractAction ) {
			final AbstractAction other = (AbstractAction) object;
			if ( getActionIdentifyingAttributesOf( this ).equals( getActionIdentifyingAttributesOf( other ) ) ) {
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

	private static Pair<Class<? extends Action>, IdentifyingAttributes>
			getActionIdentifyingAttributesOf( final Action action ) {
		// We cannot show e.g. which text is entered here!
		// Problem: We enter random text in a text field
		// This gives us a new state in which
		// entering random text in the text field is unexplored
		// Thus we do it again (loop forever)
		return Pair.of( action.getClass(), action.getTargetElement().getIdentifyingAttributes() );
	}

	@Override
	public Element getTargetElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return getActionIdentifyingAttributesOf( this ).hashCode();
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
