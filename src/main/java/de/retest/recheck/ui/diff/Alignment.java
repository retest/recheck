package de.retest.recheck.ui.diff;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.function.Function;

import de.retest.recheck.RecheckProperties;
import de.retest.recheck.ui.descriptors.Element;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Alignment {

	private static final double ELEMENT_MATCH_THRESHOLD = RecheckProperties.getInstance().elementMatchThreshold();

	/**
	 * A mapping from each child element (key) to its parent (value), based on the <em>expected</em> elements.
	 */
	private final Map<Element, Element> expectedChildParentMapping = new HashMap<>();
	/**
	 * A mapping from each child element (key) to its parent (value), based on the <em>actual</em> elements.
	 */
	private final Map<Element, Element> actualChildParentMapping = new HashMap<>();

	private final Map<Element, Element> alignment;

	public static Alignment createAlignment( final Element expected, final Element actual ) {
		return new Alignment( expected, actual );
	}

	private Alignment( final Element expected, final Element actual ) {
		final List<Element> expectedElements = flattenLeafElements( expected, expectedChildParentMapping );
		final List<Element> actualElements = flattenLeafElements( actual, actualChildParentMapping );
		log.debug(
				"Creating assignment of old to new elements, trying to find differences. We are comparing {} with {} elements.",
				expectedElements.size(), actualElements.size() );
		alignment = createAlignment( expectedElements, toIdentityMapping( actualElements ) );
		addParentAlignment();
	}

	private static List<Element> flattenLeafElements( final Element element,
			final Map<Element, Element> childParentMapping ) {
		final List<Element> flattened = new ArrayList<>();

		for ( final Element childElement : element.getContainedElements() ) {
			childParentMapping.put( childElement, element );
			if ( !childElement.hasContainedElements() ) {
				flattened.add( childElement );
			} else {
				flattened.addAll( flattenLeafElements( childElement, childParentMapping ) );
			}
		}

		return flattened;
	}

	private Map<Element, Element> createAlignment( final List<Element> expectedElements,
			final Map<Element, Element> actualElements ) {
		final Map<Element, Match> matches = new HashMap<>();
		final Queue<Element> elementsToAlign = toReverseQueue( expectedElements );
		final Map<Element, Element> alignment = new HashMap<>();

		while ( !elementsToAlign.isEmpty() ) {
			// Align elements from expected with best match.
			final Element expected = elementsToAlign.poll();

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
				log.debug( "Best match {} is below threshold with {} similarity.", bestMatch.element,
						bestMatch.similarity );
				alignment.put( expected, null );
				continue;
			}

			alignment.put( expected, bestMatch.element );
			matches.put( bestMatch.element, new Match( bestMatch.similarity, expected ) );
		}
		return alignment;
	}

	static Queue<Element> toReverseQueue( final List<Element> expectedElements ) {
		return expectedElements.stream().collect( collectingAndThen( toCollection( LinkedList::new ), queue -> {
			Collections.reverse( queue );
			return queue;
		} ) );
	}

	private static TreeSet<Match> getBestMatches( final Element expected, final Map<Element, Element> actualElements ) {
		// Try to first get the same element from actuals. This should be the standard case and, thus, cheapest.
		if ( actualElements.containsKey( expected ) ) {
			final Element identityResult = actualElements.get( expected );
			final Match bestMatch = new Match( 1.0, identityResult );
			return new TreeSet<>( Collections.singleton( bestMatch ) );
		}

		final TreeSet<Match> bestMatches = new TreeSet<>();

		for ( final Element element : actualElements.keySet() ) {
			final double similarity = match( expected, element );
			if ( similarity == 1.0 ) {
				bestMatches.add( new Match( similarity, element ) );
				return bestMatches;
			}
			bestMatches.add( new Match( similarity, element ) );
		}

		return bestMatches;
	}

	private void addParentAlignment() {
		final Map<Element, Element> alignmentCopy = new HashMap<>( alignment );
		for ( final Map.Entry<Element, Element> alignmentPair : alignmentCopy.entrySet() ) {
			final List<Element> expectedParents = getParents( alignmentPair.getKey(), expectedChildParentMapping );
			final List<Element> actualParents = getParents( alignmentPair.getValue(), actualChildParentMapping );
			final Map<Element, Element> parentAlignment =
					createAlignment( expectedParents, toIdentityMapping( actualParents ) );
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

	private List<Element> getParents( final Element element, final Map<Element, Element> childParentMapping ) {
		final List<Element> parents = new ArrayList<>();
		Element parent = childParentMapping.get( element );
		while ( parent != null ) {
			parents.add( parent );
			parent = childParentMapping.get( parent );
		}
		return parents;
	}

	static Map<Element, Element> toIdentityMapping( final List<Element> actualElements ) {
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
