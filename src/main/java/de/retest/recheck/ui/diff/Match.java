package de.retest.recheck.ui.diff;

import de.retest.recheck.ui.descriptors.Element;

public class Match implements Comparable<Match> {

	final double similarity;
	final Element element;

	public static Match of( final double similarity, final Element element ) {
		return new Match( similarity, element );
	}

	public static Match ofEqual( final Element element ) {
		return new Match( 1.0, element );
	}

	private Match( final double similarity, final Element element ) {
		this.similarity = similarity;
		this.element = element;
	}

	@Override
	public int compareTo( final Match o ) {
		final int result = Double.compare( o.similarity, similarity );
		// same similarity should not be overwritten in the Tree
		if ( result == 0 ) {
			return -1;
		}
		return result;
	}

	@Override
	public String toString() {
		return "Match[similarity='" + similarity + "', element='" + element + "']";
	}

}
