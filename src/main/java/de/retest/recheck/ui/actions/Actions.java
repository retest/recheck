package de.retest.recheck.ui.actions;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ui.descriptors.Attribute;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.ParameterType;
import de.retest.recheck.ui.descriptors.ParameterizedAttribute;
import de.retest.recheck.ui.descriptors.ParseStringAttributeDifference;
import de.retest.recheck.ui.descriptors.VariableNameAttributeDifference;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.AttributeChanges;

public class Actions {

	private Actions() {}

	private static final Logger logger = LoggerFactory.getLogger( Actions.class );

	public static ActionChangeSet createParameterValueChangeSet( final Action action,
			final ParameterizedAttribute attribute, final String value ) {
		final VariableNameAttributeDifference difference = new VariableNameAttributeDifference( attribute, value );
		return singleTonChangeSet( action, difference );
	}

	private static ActionChangeSet singleTonChangeSet( final Action action, final AttributeDifference difference ) {
		final Element element = action.getTargetElement();
		final IdentifyingAttributes identifyingAttributes = element.getIdentifyingAttributes();
		final ActionChangeSet changeSet = new ActionChangeSet();
		changeSet.getIdentAttributeChanges().add( identifyingAttributes, difference );
		return changeSet;
	}

	public static Action convertActionIfPossible( final Action action, final ValueProvider provider ) {
		if ( isParameterizedAction( action, provider ) ) {
			return convertAction( (ParameterizedAction) action, provider );
		}
		return action;
	}

	private static Action convertAction( final ParameterizedAction action, final ValueProvider provider ) {
		final Set<ActionParameter> parameters = action.getActionParameter();
		final Set<ActionParameter> newParameters = new HashSet<>( parameters.size() );

		for ( final ActionParameter parameter : parameters ) {
			newParameters.add( convertActionParameterIfPossible( provider, parameter ) );
		}
		final Action changes = action.applyChanges( newParameters );

		final IdentifyingAttributes attributes = action.getTargetElement().getIdentifyingAttributes();
		final ActionChangeSet changeSet = new ActionChangeSet();
		final AttributeChanges attributeChanges = changeSet.getIdentAttributeChanges();

		for ( final Attribute attribute : attributes.getAttributes() ) {
			final AttributeDifference difference = createAttributeDifferenceIfPossible( attribute, provider );
			if ( difference != null ) {
				attributeChanges.add( attributes, difference );
			}
		}

		return changes.applyChanges( changeSet );
	}

	private static ActionParameter convertActionParameterIfPossible( final ValueProvider provider,
			final ActionParameter parameter ) {
		final String variableName = parameter.getVariableName();
		if ( isVariableNameInvalid( variableName ) ) {
			logger.debug( "ActionParameter {} was not validated with name {}.", parameter, variableName );
			return parameter;
		}
		final String variableValue = provider.get( variableName );
		if ( StringUtils.isEmpty( variableValue ) || isVariableValueInvalid( parameter, variableValue ) ) {
			logger.debug( "ActionParameter {} was not validated with value {}.", parameter, variableValue );
			return parameter;
		}
		return parameter.setValue( variableValue );
	}

	private static AttributeDifference createAttributeDifferenceIfPossible( final Attribute attribute,
			final ValueProvider provider ) {
		if ( attribute instanceof ParameterizedAttribute ) {
			final ParameterizedAttribute param = (ParameterizedAttribute) attribute;
			final String variableName = param.getVariableName();
			if ( isVariableNameInvalid( variableName ) ) {
				logger.debug( "Attribute {} was not validated with name {}.", attribute, variableName );
				return null;
			}
			final String variableValue = provider.get( variableName );
			if ( StringUtils.isEmpty( variableValue ) || isVariableValueInvalid( param, variableValue ) ) {
				logger.debug( "Attribute {} was not validated with value {}.", attribute, variableValue );
				return null;
			}
			return new ParseStringAttributeDifference( param, variableValue );
		}
		return null;
	}

	public static boolean validateAction( final Action action, final ValueProvider provider ) {
		return !isParameterizedAction( action, provider ) || validateAction( (ParameterizedAction) action, provider );
	}

	private static boolean validateAction( final ParameterizedAction action, final ValueProvider provider ) {
		logger.debug( "Validating parameterized action {}.", action );
		return validateActionParameters( action, provider ) && validateElementAttributes( action, provider );
	}

	private static boolean validateActionParameters( final ParameterizedAction action, final ValueProvider provider ) {
		final Set<ActionParameter> parameters = action.getActionParameter();
		for ( final ActionParameter parameter : parameters ) {
			final String variableName = parameter.getVariableName();
			if ( isVariableNameInvalid( variableName ) ) {
				logger.debug( "ActionParameter {} was not validated with name {}.", parameter, variableName );
				continue;
			}

			final String variableValue = provider.get( variableName );
			if ( StringUtils.isNotEmpty( variableValue ) && isVariableValueInvalid( parameter, variableValue ) ) {
				logger.debug( "ActionParameter {} was not validated with value {}.", parameter, variableValue );
				return false;
			}
		}
		return true;
	}

	private static boolean validateElementAttributes( final ParameterizedAction action, final ValueProvider provider ) {
		final IdentifyingAttributes attributes = action.getTargetElement().getIdentifyingAttributes();
		for ( final Attribute attribute : attributes.getAttributes() ) {
			if ( attribute instanceof ParameterizedAttribute ) {
				final ParameterizedAttribute param = (ParameterizedAttribute) attribute;
				final String variableName = param.getVariableName();
				if ( isVariableNameInvalid( variableName ) ) {
					logger.debug( "Attribute {} was not validated with name {}.", param, variableName );
					continue;
				}
				final String variableValue = provider.get( variableName );
				if ( StringUtils.isNotEmpty( variableValue ) && isVariableValueInvalid( param, variableValue ) ) {
					logger.debug( "Attribute {} was not validated with value {}.", param, variableValue );
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isParameterizedAction( final Action action, final ValueProvider provider ) {
		return provider != null && action instanceof ParameterizedAction;
	}

	private static boolean isVariableValueInvalid( final ActionParameter parameter, final String value ) {
		return !ParameterType.getType( parameter.getType() ).canParse( value );
	}

	private static boolean isVariableValueInvalid( final ParameterizedAttribute attribute, final String value ) {
		return !attribute.getType().canParse( value );
	}

	private static boolean isVariableNameInvalid( final String name ) {
		return StringUtils.isEmpty( name );
	}
}
