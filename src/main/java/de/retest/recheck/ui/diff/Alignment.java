package de.retest.recheck.ui.diff;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.RecheckProperties;
import de.retest.recheck.ui.descriptors.Element;

public final class Alignment {

	private static final Logger logger = LoggerFactory.getLogger( Alignment.class );

	private static final double ELEMENT_MATCH_THRESHOLD = RecheckProperties.getInstance().elementMatchThreshold();

	/**
	 * A mapping from each child element (key) to its parent (value), based on the <em>expected</em> elements.
	 */
	private final Map<Element, Element> expectedMapOfElementTree = new HashMap<>();
	/**
	 * A mapping from each child element (key) to its parent (value), based on the <em>actual</em> elements.
	 */
	private final Map<Element, Element> actualMapOfElementTree = new HashMap<>();

	private final Map<Element, Element> alignment;

	public static Alignment createAlignment( final Element expected, final Element actual ) {
		return new Alignment( expected, actual );
	}

	private Alignment( final Element expected, final Element actual ) {
		final List<Element> expectedElements = flattenLeafElements( expected, expectedMapOfElementTree );
		final List<Element> actualElements = flattenLeafElements( actual, actualMapOfElementTree );
		logger.debug(
				"Creating assignment of old to new elements, trying to find differences. We are comparing {} with {} elements.",
				expectedElements.size(), actualElements.size() );
		alignment = createAlignment( expectedElements, toMapping( actualElements ) );
		addParentAlignment();
	}

	private static List<Element> flattenLeafElements( final Element element,
			final Map<Element, Element> mapOfElementTree ) {
		final List<Element> flattened = new ArrayList<>();

		for ( final Element childElement : element.getContainedElements() ) {
			mapOfElementTree.put( childElement, element );
			if ( !childElement.hasContainedElements() ) {
				flattened.add( childElement );
			} else {
				flattened.addAll( flattenLeafElements( childElement, mapOfElementTree ) );
			}
		}

		return flattened;
	}

	private Map<Element, Element> createAlignment( final List<Element> expectedElements,
			final Map<Element, Element> actualElements ) {
		final Map<Element, Match> matches = new HashMap<>();
		final Stack<Element> elementsToAlign = toStack( expectedElements );
		final Map<Element, Element> alignment = new HashMap<>();

		while ( !elementsToAlign.isEmpty() ) {
			// Align elements from expected with best match.
			final Element expected = elementsToAlign.pop();

			final TreeSet<Match> bestMatches = getBestMatches( expected, actualElements );

			Match bestMatch = bestMatches.pollFirst();
			while ( bestMatch != null ) {

				// If a best match has multiple alignments, delete all but overall best.
				if ( matches.containsKey( bestMatch.element ) ) {
					final Match previousMatch = matches.get( bestMatch.element );
					if ( bestMatch.similarity <= previousMatch.similarity ) {
						assert bestMatch.similarity != 1.0 : "bestMatch and previousMatch have a match of 100%? At least paths should differ! "
								+ bestMatch.element.getIdentifyingAttributes().toFullString() + " == "
								+ previousMatch.element.getIdentifyingAttributes().toFullString();

						// Case: bestMatch is already taken for other element.
						bestMatch = bestMatches.pollFirst();
						continue;

					} else {
						// Case: bestMatch takes this element.
						alignment.remove( previousMatch.element );
						elementsToAlign.add( previousMatch.element );
						break;

					}
				} else {
					break;
				}

			}

			if ( bestMatch == null ) {
				alignment.put( expected, null );
				continue;
			}

			if ( bestMatch.similarity < ELEMENT_MATCH_THRESHOLD ) {
				logger.debug( "Best match {} is below threshold with {} similarity.", bestMatch.element,
						bestMatch.similarity );
				alignment.put( expected, null );
				continue;
			}

			alignment.put( expected, bestMatch.element );
			matches.put( bestMatch.element, new Match( bestMatch.similarity, expected ) );
		}
		return alignment;
	}

	private static Stack<Element> toStack( final List<Element> expectedElements ) {
		final Stack<Element> elementsToAlign = new Stack<>();
		elementsToAlign.addAll( expectedElements );
		reverse( elementsToAlign );
		return elementsToAlign;
	}

	private static TreeSet<Match> getBestMatches( final Element expected, final Map<Element, Element> actualElements ) {
		final TreeSet<Match> result = new TreeSet<>();

		final Element identityResult = actualElements.get( expected );
		if ( identityResult != null ) {
			// Try to first get the same element from actuals. This should be the standard case and thus cheapest.
			result.add( new Match( 1.0, identityResult ) );

		} else {

			for ( final Element element : actualElements.keySet() ) {
				final double similarity = match( expected, element );
				if ( similarity == 1.0d ) {
					result.add( new Match( similarity, element ) );
					return result;
				}
				result.add( new Match( similarity, element ) );
			}
		}

		return result;
	}

	private void addParentAlignment() {
		final Map<Element, Element> alignmentCopy = new HashMap<>( alignment );
		for ( final Map.Entry<Element, Element> alignmentPair : alignmentCopy.entrySet() ) {
			final List<Element> expectedParents = getParents( alignmentPair.getKey(), expectedMapOfElementTree );
			final List<Element> actualParents = getParents( alignmentPair.getValue(), actualMapOfElementTree );
			final Map<Element, Element> parentAlignment =
					createAlignment( expectedParents, toMapping( actualParents ) );
			for ( final Map.Entry<Element, Element> parentAlignmentPair : parentAlignment.entrySet() ) {
				final Element aligned = alignment.get( parentAlignmentPair.getKey() );
				if ( aligned == null ) {
					alignment.put( parentAlignmentPair.getKey(), parentAlignmentPair.getValue() );
					continue;
				}
				if ( parentAlignmentPair.getValue() == null ) {
					continue;
				}
				if ( match( parentAlignmentPair.getKey(),
						parentAlignmentPair.getValue() ) > match( parentAlignmentPair.getKey(), aligned ) ) {
					alignment.put( parentAlignmentPair.getKey(), parentAlignmentPair.getValue() );
				}
			}
		}
	}

	private List<Element> getParents( final Element element, final Map<Element, Element> treeMap ) {
		final List<Element> result = new ArrayList<>();
		Element parent = treeMap.get( element );
		while ( parent != null ) {
			result.add( parent );
			parent = treeMap.get( parent );
		}
		return result;
	}

	static Map<Element, Element> toMapping( final List<Element> actualElements ) {
		return actualElements.stream().collect( toMap( Function.identity(), Function.identity() ) );
	}

	private static double match( final Element expected, final Element bestMatch ) {
		return expected.getIdentifyingAttributes().match( bestMatch.getIdentifyingAttributes() );
	}

	/**
	 * Get the aligned actual element to an expected element.
	 *
	 * @param expected
	 *            expected element
	 * @return aligned actual element
	 */
	public Element getActual( final Element expected ) {
		return alignment.get( expected );
	}

	@Override
	public String toString() {
		return alignment.toString();
	}

	@Override
	public int hashCode() {
		return alignment.hashCode();
	}

	@Override
	public boolean equals( final Object other ) {
		if ( other instanceof Alignment ) {
			final Alignment otherAlignment = (Alignment) other;
			return alignment.equals( otherAlignment.alignment );
		}
		return false;
	}
}
