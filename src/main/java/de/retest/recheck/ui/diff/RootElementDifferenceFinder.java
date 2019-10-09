package de.retest.recheck.ui.diff;

import static de.retest.recheck.Properties.WINDOW_CONTAINED_CHILDREN_MATCH_THRESHOLD_DEFAULT;
import static de.retest.recheck.Properties.WINDOW_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY;
import static de.retest.recheck.Properties.WINDOW_MATCH_THRESHOLD_DEFAULT;
import static de.retest.recheck.Properties.WINDOW_MATCH_THRESHOLD_PROPERTY;
import static de.retest.recheck.Properties.getConfiguredDouble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.ElementUtil;
import de.retest.recheck.ui.descriptors.RootElement;

public class RootElementDifferenceFinder {

	private static final org.slf4j.Logger logger =
			org.slf4j.LoggerFactory.getLogger( RootElementDifferenceFinder.class );

	private final ElementDifferenceFinder elementDifferenceFinder;

	private final Double minimumWindowMatch =
			getConfiguredDouble( WINDOW_MATCH_THRESHOLD_PROPERTY, WINDOW_MATCH_THRESHOLD_DEFAULT );
	private final Double minimumContainedComponentsMatchMatch = getConfiguredDouble(
			WINDOW_CONTAINED_CHILDREN_MATCH_THRESHOLD_PROPERTY, WINDOW_CONTAINED_CHILDREN_MATCH_THRESHOLD_DEFAULT );

	public RootElementDifferenceFinder( final DefaultValueFinder defaultValueFinder ) {
		elementDifferenceFinder = new ElementDifferenceFinder( defaultValueFinder );
	}

	public List<RootElementDifference> findDifferences( final List<RootElement> expecteds,
			final List<RootElement> actuals ) {
		final List<RootElementDifference> differences = new ArrayList<>();
		final List<RootElement> copyOfActuals = new ArrayList<>( actuals );
		for ( final RootElement expected : expecteds ) {
			final RootElementDifference difference =
					findDifference( expected, findAndRemove( expected, copyOfActuals ) );
			if ( difference != null ) {
				differences.add( difference );
			}
		}
		for ( final RootElement actual : copyOfActuals ) {
			differences.add( findDifference( null, actual ) );
		}
		return differences;
	}

	private RootElement findAndRemove( final RootElement expected, final List<RootElement> actuals ) {
		double bestMatch = 0.0;
		RootElement bestWindow = null;
		for ( final RootElement actual : actuals ) {
			if ( actual.getIdentifyingAttributes().equals( expected.getIdentifyingAttributes() ) ) {
				bestMatch = 1.0d;
				bestWindow = actual;
				break;
			}
			final double currentMatch = actual.getIdentifyingAttributes().match( expected.getIdentifyingAttributes() );
			if ( currentMatch > bestMatch ) {
				bestMatch = currentMatch;
				bestWindow = actual;
			}
		}
		if ( bestWindow == null ) {
			logger.info( "No window at all found!" );
			return null;
		}
		if ( bestMatch >= minimumWindowMatch ) {
			actuals.remove( bestWindow );
			return bestWindow;
		}
		logger.info( "Best match of window {} did not exceed MATCH_THRESHOLD with {}: {}",
				expected.getIdentifyingAttributes(), bestMatch, bestWindow );
		final double containedComponentsMatch = compareContainedComponents( expected, bestWindow );
		if ( containedComponentsMatch >= minimumContainedComponentsMatchMatch ) {
			logger.info( "Best match of window has a match of contained components of {}.", containedComponentsMatch );
			return bestWindow;
		}
		return null;
	}

	private double compareContainedComponents( final RootElement expected, final RootElement bestWindow ) {
		return getNumberOfChildDifferences( expected, bestWindow )
				/ (getNumberOfChildComponents( expected, bestWindow ) + 1.0);
	}

	private int getNumberOfChildDifferences( final RootElement expected, final RootElement bestWindow ) {
		final Collection<ElementDifference> differences =
				elementDifferenceFinder.findChildDifferences( expected, bestWindow );
		int count = 0;
		for ( final ElementDifference elementDifference : differences ) {
			count += elementDifference.getImmediateDifferences().size();
		}
		logger.debug( "Found {} child differences for window {} and best match {}.", count, expected, bestWindow );
		return count;
	}

	private int getNumberOfChildComponents( final RootElement expected, final RootElement bestWindow ) {
		final int expectedChildCount = ElementUtil.flattenChildElements( expected ).size();
		final int bestWindowChildCount = ElementUtil.flattenChildElements( bestWindow ).size();
		logger.debug( "Window {} contained {} components, and and best match {} contained {} components.",
				new Object[] { expected, expectedChildCount, bestWindow, bestWindowChildCount } );
		return Math.max( expectedChildCount, bestWindowChildCount );
	}

	public RootElementDifference findDifference( final RootElement expected, final RootElement actual ) {
		final long startTime = System.currentTimeMillis();
		final ElementDifference elementDifference = elementDifferenceFinder.differenceFor( expected, actual );
		logger.debug( "Finding differences for window took {}ms.", System.currentTimeMillis() - startTime );
		return elementDifference != null ? new RootElementDifference( elementDifference, expected, actual ) : null;
	}

}
