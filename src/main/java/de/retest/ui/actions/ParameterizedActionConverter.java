package de.retest.ui.actions;

import static de.retest.ui.descriptors.DefaultAttribute.parameterTypeAttribute;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.retest.ui.Path;
import de.retest.ui.descriptors.Attribute;
import de.retest.ui.descriptors.Attributes;
import de.retest.ui.descriptors.DefaultAttribute;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.OutlineAttribute;
import de.retest.ui.descriptors.PathAttribute;
import de.retest.ui.descriptors.RetestIdProviderUtil;
import de.retest.ui.descriptors.RootElement;
import de.retest.ui.descriptors.StringAttribute;
import de.retest.ui.descriptors.WeightedTextAttribute;

public class ParameterizedActionConverter {

	public static ActionParameter[] toActionParameter( final ParameterizedAction action, final String prefix ) {
		return new ParameterizedActionConverter( prefix ).toActionParameter( action );
	}

	public static ParameterizedAction toAction( final Collection<ActionParameter> params, final String prefix ) {
		return new ParameterizedActionConverter( prefix ).toAction( params );
	}

	private final String prefix;

	ParameterizedActionConverter( final String prefix ) {
		this.prefix = prefix;
	}

	public ActionParameter[] toActionParameter( final ParameterizedAction action ) {
		final Collection<ActionParameter> result = new ArrayList<>();
		result.addAll( toActionParameter( action.getTargetElement().getIdentifyingAttributes() ) );
		// we ignore state attributes here
		result.addAll( prefix( action.getActionParameter() ) );
		return result.toArray( new ActionParameter[result.size()] );
	}

	private Set<ActionParameter> prefix( final Set<ActionParameter> actionParameter ) {
		final Set<ActionParameter> result = new HashSet<>();
		for ( final ActionParameter parameter : actionParameter ) {
			result.add( new ActionParameter( prefix + "." + parameter.getName(), parameter.getValue(),
					parameter.getType() ) );
		}
		return result;
	}

	private Set<ActionParameter> fromPrefix( final Collection<ActionParameter> params ) {
		final Set<ActionParameter> result = new HashSet<>();
		for ( final ActionParameter actionParameter : params ) {
			if ( actionParameter.getName().startsWith( prefix + "." ) && actionParameter.getAttributeClass() == null ) {
				final String scrubbedName = removePrefix( actionParameter.getName() );
				result.add(
						new ActionParameter( scrubbedName, actionParameter.getValue(), actionParameter.getType() ) );
			}
		}
		return result;
	}

	private String removePrefix( final String nameWithPrefix ) {
		if ( !nameWithPrefix.startsWith( prefix ) ) {
			throw new IllegalArgumentException(
					"Given name " + nameWithPrefix + "must start with prefix, but didn't." );
		}
		return nameWithPrefix.substring( prefix.length() + 1, nameWithPrefix.length() );
	}

	private Collection<ActionParameter> toActionParameter( final IdentifyingAttributes identifyingAttributes ) {
		final Collection<ActionParameter> result = new ArrayList<>();
		for ( final Attribute attribute : identifyingAttributes.getAttributes() ) {
			result.add( toActionParameter( attribute ) );
		}
		return result;
	}

	private ActionParameter toActionParameter( final Attribute attribute ) {
		String value = null;
		if ( attribute.getValue() != null ) {
			value = attribute.getValue().toString();
		}
		return new ActionParameter( prefix + "." + attribute.getKey(), value, parameterTypeAttribute, null,
				attribute.getClass().getName() );
	}

	private Attribute fromActionParameter( final ActionParameter actionParameter ) {
		try {
			final String attributeClassname = actionParameter.getAttributeClass();
			if ( attributeClassname.equals( PathAttribute.class.getName() ) ) {
				return new PathAttribute( Path.fromString( actionParameter.getValue() ) );
			}
			if ( attributeClassname.equals( OutlineAttribute.class.getName() ) ) {
				return OutlineAttribute.create( OutlineAttribute.parse( actionParameter.getValue() ) );
			}
			if ( attributeClassname.equals( DefaultAttribute.class.getName() ) ) {
				return new DefaultAttribute( removePrefix( actionParameter.getName() ), actionParameter.getValue() );
			}
			if ( attributeClassname.equals( StringAttribute.class.getName() ) ) {
				return new StringAttribute( removePrefix( actionParameter.getName() ), actionParameter.getValue() );
			}
			if ( attributeClassname.equals( WeightedTextAttribute.class.getName() ) ) {
				return new WeightedTextAttribute( removePrefix( actionParameter.getName() ),
						actionParameter.getValue() );
			}
			final Class<?> attributeClass = Class.forName( attributeClassname );
			final Constructor<?> constructor = attributeClass.getConstructor( String.class );
			return (Attribute) constructor.newInstance( actionParameter.getValue() );
		} catch ( final Exception e ) {
			throw new RuntimeException( e );
		}
	}

	private Collection<Attribute> fromActionParameter( final Collection<ActionParameter> params ) {
		final Collection<Attribute> result = new ArrayList<>();
		for ( final ActionParameter actionParameter : params ) {
			if ( actionParameter.getName().startsWith( prefix ) && actionParameter.getAttributeClass() != null ) {
				result.add( fromActionParameter( actionParameter ) );
			}
		}
		return result;
	}

	public ParameterizedAction toAction( final Collection<ActionParameter> params ) {
		final IdentifyingAttributes identifyingAttributes = new IdentifyingAttributes( fromActionParameter( params ) );
		final String retestId = RetestIdProviderUtil.getRetestId( identifyingAttributes );
		final RootElement parent =
				new RootElement( retestId, identifyingAttributes, new Attributes(), null, null, 0, null );

		final Element element = Element.create( retestId, parent, identifyingAttributes, new Attributes() );
		return new ParameterizedAction( element, null, fromPrefix( params ) );
	}
}
