package de.retest.ui;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;

import de.retest.ui.actions.Action;
import de.retest.ui.actions.TargetNotFoundException;
import de.retest.ui.components.Component;
import de.retest.ui.components.RootContainer;
import de.retest.ui.descriptors.Element;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.image.Screenshot;

public class TargetFinder<T> {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( TargetFinder.class );

	public static <T> ImmutablePair<TargetNotFoundException, Component<T>> findTargetComponent( final Element element,
			final List<RootContainer<T>> targetableWindows, final Screenshot[] windowsScreenshots ) {
		return new TargetFinder<>( targetableWindows, windowsScreenshots ).findTargetComponent( null, element );
	}

	public static <T> ImmutablePair<TargetNotFoundException, Component<T>> findTargetComponent( final Action action,
			final List<RootContainer<T>> targetableWindows, final Screenshot[] windowsScreenshots ) {
		return new TargetFinder<>( targetableWindows, windowsScreenshots ).findTargetComponent( action,
				action.getTargetElement() );
	}

	private final List<RootContainer<T>> targetableWindows;
	private final Screenshot[] windowsScreenshots;
	/**
	 * This is intentionally not a system-property as it should not be adaptable from customers. But we want it to be a
	 * system-wide constant instead of a magic number.
	 */
	public static final double MATCH_THRESHOLD = 0.7;

	private TargetFinder( final List<RootContainer<T>> targetableWindows, final Screenshot[] windowsScreenshots ) {
		this.targetableWindows = targetableWindows;
		this.windowsScreenshots = windowsScreenshots;
	}

	private ImmutablePair<TargetNotFoundException, Component<T>> findTargetComponent( final Action action,
			final Element element ) {
		if ( action != null && !action.getTargetElement().equals( element ) ) {
			throw new IllegalArgumentException(
					"If an action is passed the element must be retrieved from Action#getTargetElement()" );
		}

		final IdentifyingAttributes identifyingAttributes = element.getIdentifyingAttributes();
		final Component<T> bestMatch = getBestMatchForAllWindows( element );
		final double match = bestMatch != null ? bestMatch.match( identifyingAttributes ) : 0.0d;

		if ( match >= TargetFinder.MATCH_THRESHOLD ) {
			return new ImmutablePair<>( null, bestMatch );
		}

		if ( bestMatchHasSameTypeAndText( identifyingAttributes, bestMatch ) ) {
			logger.error( "Our threshold would reject this component, but it seems to be correct: {} - {}",
					bestMatch.getIdentifyingAttributes().toFullString(), identifyingAttributes.toFullString() );
			return new ImmutablePair<>( null, bestMatch );
		}

		logError( identifyingAttributes );

		if ( bestMatch != null ) {
			final String percentageOfBestMatch = String.format( "%.2f", match * 100.0 );
			logger.error( "Best with {}% match is {}", percentageOfBestMatch,
					bestMatch.getIdentifyingAttributes().toFullString() );
			final String message = "Could not find component " + identifyingAttributes.toFullString() + " in "
					+ targetableWindows.size() + " windows! Best with " + percentageOfBestMatch + "% match is "
					+ bestMatch.getIdentifyingAttributes().toFullString();
			final TargetNotFoundException tnfe =
					new TargetNotFoundException( action, bestMatch.getElement(), windowsScreenshots, message );
			return new ImmutablePair<>( tnfe, null );
		}

		final TargetNotFoundException tnfe = new TargetNotFoundException( action, null, windowsScreenshots,
				"Could not resolve component " + identifyingAttributes + "! No best match found!" );
		return new ImmutablePair<>( tnfe, null );
	}

	private boolean bestMatchHasSameTypeAndText( final IdentifyingAttributes identifyingAttributes,
			final Component<T> bestMatch ) {
		if ( bestMatch == null ) {
			return false;
		}
		return sameType( bestMatch, identifyingAttributes ) && sameText( bestMatch, identifyingAttributes );
	}

	private void logError( final IdentifyingAttributes identifyingAttributes ) {
		logger.error( "Could not find component {} in {} window(s): ", identifyingAttributes.toFullString(),
				targetableWindows.size() );
		for ( int i = 0; i < targetableWindows.size(); i++ ) {
			final RootContainer<T> window = targetableWindows.get( i );
			logger.error( "Window no. {}: {}", i + 1, window.getTextWithComponents() );
		}
	}

	private Component<T> getBestMatchForAllWindows( final Element element ) {
		double bestMatch = 0.0;
		Component<T> bestMatchComponent = null;
		final IdentifyingAttributes identifyingAttributes = element.getIdentifyingAttributes();
		for ( final RootContainer<T> window : targetableWindows ) {
			final Component<T> currentComponent = window.getBestMatch( identifyingAttributes );
			if ( currentComponent == null ) {
				continue;
			}
			if ( currentComponent.getIdentifyingAttributes().equals( identifyingAttributes ) ) {
				return currentComponent;
			}
			final double currentMatch = currentComponent.match( identifyingAttributes );
			if ( currentMatch > bestMatch ) {
				bestMatch = currentMatch;
				bestMatchComponent = currentComponent;
			}
		}
		return bestMatchComponent;
	}

	private boolean sameText( final Component<T> bestMatch, final IdentifyingAttributes identifyingAttributes ) {
		return Objects.equals( bestMatch.getIdentifyingAttributes().get( "text" ),
				identifyingAttributes.get( "text" ) );
	}

	private boolean sameType( final Component<T> bestMatch, final IdentifyingAttributes identifyingAttributes ) {
		return bestMatch.getIdentifyingAttributes().getType().equals( identifyingAttributes.getType() );
	}
}
