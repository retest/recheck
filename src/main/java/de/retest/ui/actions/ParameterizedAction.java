package de.retest.ui.actions;

import static de.retest.ui.descriptors.StringAttribute.parameterTypeClass;
import static de.retest.ui.descriptors.StringAttribute.parameterTypeString;
import static de.retest.util.ObjectUtil.nextHashCode;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.ui.Environment;
import de.retest.ui.components.Component;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.ParameterType;
import de.retest.ui.image.Screenshot;
import de.retest.ui.review.ActionChangeSet;

@XmlRootElement
public class ParameterizedAction extends AbstractAction {

	private static final Logger logger = LoggerFactory.getLogger( ParameterizedAction.class );

	private static final long serialVersionUID = 1L;

	public static final String ACTION_CLASS_PARAM = "actionClass";
	public static final String DESCRIPTION_PARAM = "description";

	@XmlElement
	private final Set<ActionParameter> actionParameter;

	// For JAXB, protected for subclasses
	protected ParameterizedAction() {
		actionParameter = new HashSet<>();
	}

	public ParameterizedAction( final ParameterizedAction data ) {
		this( data.getTargetElement(), data.getWindowsScreenshots(), data.getActionParameter() );
	}

	public ParameterizedAction( final Element element, final Screenshot[] windowScreenshots, final Class<?> actionClass,
			final String description, final ActionParameter... params ) {
		super( element, windowScreenshots );

		if ( actionClass == null ) {
			throw new IllegalArgumentException( "Cannot create a ParameterizedAction without action class!" );
		}
		if ( description == null ) {
			throw new IllegalArgumentException( "Cannot create a ParameterizedAction without description!" );
		}

		final Set<ActionParameter> tmpActionParams = new HashSet<>( Arrays.asList( params ) );
		tmpActionParams.add( actionClassParam( actionClass ) );
		tmpActionParams.add( descriptionParam( description ) );
		actionParameter = Collections.unmodifiableSet( tmpActionParams );
	}

	ParameterizedAction( final Element element, final Screenshot[] windowScreenshots,
			final Set<ActionParameter> params ) {
		super( element, windowScreenshots );
		// Iterating over a doubly wrapped unmodifiable causes StackOverflowError.
		actionParameter = new HashSet<>( params );
		if ( getParameter( ACTION_CLASS_PARAM ) == null ) {
			throw new IllegalArgumentException( "Cannot create a ParameterizedAction without action class!" );
		}
		if ( getParameter( DESCRIPTION_PARAM ) == null ) {
			throw new IllegalArgumentException( "Cannot create a ParameterizedAction without description!" );
		}
	}

	public Set<ActionParameter> getActionParameter() {
		return new HashSet<>( actionParameter );
	}

	@Override
	public Action applyChanges( final ActionChangeSet reviewResult ) {
		return new ParameterizedAction( element.applyChanges( reviewResult ), getWindowsScreenshots(),
				new HashSet<>( actionParameter ) );
	}

	public ParameterizedAction applyChanges( final ActionParameter... changedParam ) {
		final Map<String, ActionParameter> parameterToReplaceByName = Arrays.stream( changedParam ) //
				.collect( Collectors.toMap( ActionParameter::getName, Function.identity() ) );

		final Set<ActionParameter> newActionParameter = actionParameter.stream() //
				.map( param -> parameterToReplaceByName.getOrDefault( param.getName(), param ) ) //
				.collect( Collectors.toSet() );

		return new ParameterizedAction( element, getWindowsScreenshots(), newActionParameter );
	}

	public ParameterizedAction applyChanges( final Set<ActionParameter> changedParameters ) {
		return new ParameterizedAction( element, getWindowsScreenshots(), changedParameters );
	}

	@Override
	public String createDescription() {
		return getParameterValue( DESCRIPTION_PARAM );
	}

	public ActionParameter getParameter( final String name ) {
		for ( final ActionParameter param : actionParameter ) {
			if ( param.getName().equals( name ) ) {
				return param;
			}
		}
		return null;
	}

	@SuppressWarnings( "unchecked" )
	public <T> T getParameterValue( final String name ) {
		final ActionParameter parameter = getParameter( name );
		if ( parameter == null ) {
			return null;
		}
		final String value = parameter.getValue();
		final ParameterType parameterType = ParameterType.getType( parameter.getType() );
		try {
			final Object parse = parameterType.parse( value );
			return (T) parse;
		} catch ( final Exception e ) {
			logger.error( "Exception parsing parameter {} of type {} with value {}.", name, value, parameterType );
			return null;
		}
	}

	@Override
	public void execute( final Component<?> component ) throws TargetNotFoundException {
		final Action action = getAction();
		action.execute( component );
	}

	// Some actions may execute in environment (e.g. TableCellFocusAction).
	@Override
	public <T> ActionExecutionResult execute( final Environment<T> environment ) {
		// This is a bad hack to avoid a stack overflow if it's not overwritten.
		if ( isExecuteNotOverwritten() ) {
			return super.execute( environment );
		}
		final Action action = getAction();
		return action.execute( environment );
	}

	private boolean isExecuteNotOverwritten() {
		try {
			return getActionClass().getMethod( "execute", Environment.class ).getDeclaringClass()
					.equals( ParameterizedAction.class );
		} catch ( final Exception e ) {
			throw new RuntimeException( e );
		}
	}

	protected Action getAction() {
		try {
			final Class<?> actionClass = getActionClass();
			if ( actionClass.equals( ParameterizedAction.class ) ) {
				throw new RuntimeException(
						"Cannot execute a ParameterizedAction directly, subclass and pass the class in the constructor!" );
			}
			final Constructor<?> constructor = actionClass.getConstructor( ParameterizedAction.class );
			return (Action) constructor.newInstance( this );
		} catch ( final Exception e ) {
			throw new RuntimeException( "Exception execution action " + createDescription(), e );
		}
	}

	protected static ActionParameter actionClassParam( final Class<?> clazz ) {
		return new ActionParameter( ACTION_CLASS_PARAM, clazz.getName(), parameterTypeClass );
	}

	protected static ActionParameter descriptionParam( final String description ) {
		return new ActionParameter( DESCRIPTION_PARAM, description, parameterTypeString );
	}

	public Class<?> getActionClass() {
		return getParameterValue( ACTION_CLASS_PARAM );
	}

	@Override
	public String toString() {
		return getParameterValue( DESCRIPTION_PARAM );
	}

	@Override
	public int hashCode() {
		int result = 0;
		for ( final ActionParameter param : actionParameter ) {
			result = nextHashCode( result, param.hashCode() );
		}
		return result;
	}

	@Override
	public boolean equals( final Object object ) {
		if ( this == object ) {
			return true;
		}
		if ( !(object instanceof ParameterizedAction) ) {
			return false;
		}
		final ParameterizedAction other = (ParameterizedAction) object;
		if ( actionParameter.size() != other.actionParameter.size() ) {
			return false;
		}
		for ( final ActionParameter param : actionParameter ) {
			final ActionParameter otherParam = other.getParameter( param.getName() );
			if ( !param.equals( otherParam ) ) {
				return false;
			}
		}
		return true;
	}

}
