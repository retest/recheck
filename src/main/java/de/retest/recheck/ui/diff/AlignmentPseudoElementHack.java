package de.retest.recheck.ui.diff;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.retest.recheck.ui.descriptors.Element;

/**
 * Hack to fix pseudo element handling.
 *
 * This class contains knowledge about HTML Pseudo Elements, but this should be in recheck-web only.
 */
class AlignmentPseudoElementHack {

	/**
	 * A mapping from each pseudo element (key) to its parent (value), based on the <em>expected</em> elements.
	 */
	final Map<Element, Element> expectedPseudoElementsMapping = new HashMap<>();
	/**
	 * A mapping from each child element (key) to its parent (value), based on the <em>actual</em> elements.
	 */
	final Map<Element, Element> actualPseudoElementsMapping = new HashMap<>();

	void alignPseudoElements( final Map<Element, Element> alignment ) {
		expectedPseudoElementsMapping.forEach( ( expectedPseudo, expectedParent ) -> {

			final Element actualParent = alignment.get( expectedParent );
			if ( actualParent != null ) {

				actualPseudoElementsMapping.entrySet().stream() //
						.filter( entry -> entry.getValue().equals( actualParent ) ) //
						.map( Entry::getKey ) //
						// here we have a list of candidates based on parent assignment
						.map( actualPseudoCandidate -> match( expectedPseudo, actualPseudoCandidate ) ) //
						.filter( match -> match.similarity > Alignment.ELEMENT_MATCH_THRESHOLD ) //
						.sorted().findFirst() //
						.ifPresent( m -> {
							alignment.put( expectedPseudo, m.element );
						} );

			}

		} );
	}

	static boolean isLeaf( final Element element, final Map<Element, Element> pseudoElementsMapping ) {
		boolean isLeaf = true;
		for ( final Element child : element.getContainedElements() ) {
			if ( isPseudoElement( child ) ) {
				pseudoElementsMapping.put( child, element );
			} else {
				isLeaf = false;
			}
		}
		return isLeaf;
	}

	private static boolean isPseudoElement( final Element e ) {
		return e.getIdentifyingAttributes().getType().startsWith( "::" );
	}

	private static Match match( final Element expected, final Element actualCandidate ) {
		return Match.of( Alignment.match( expected, actualCandidate ), actualCandidate );
	}
}
