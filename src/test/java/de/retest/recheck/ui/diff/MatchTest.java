package de.retest.recheck.ui.diff;

import static de.retest.recheck.ui.diff.ElementBuilder.toAttributes;
import static org.mockito.Mockito.mock;

import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.Path;
import de.retest.recheck.ui.PathElement;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;

class MatchTest {

	public static final double ONE_SIMILARITY = 1.0d / IdentifyingAttributes.PERFECT_SIMILARITY;

	@Test
	void test_better_comp_always_comes_out_first() {
		final Element comp1 = getElement( comp1.class, 1 );
		final Match match1 = new Match( 0.9d + ONE_SIMILARITY, comp1 );

		final Element comp2 = getElement( comp2.class, 2 );
		final Match match2 = new Match( 0.9d, comp2 );

		final TreeSet<Match> bestMatches = new TreeSet<>();
		bestMatches.add( match1 );
		bestMatches.add( match2 );

		Assertions.assertThat( bestMatches.pollFirst().element ).isSameAs( comp1 );
		Assertions.assertThat( bestMatches.pollFirst().element ).isSameAs( comp2 );
	}

	@Test
	void same_score_doesnt_overwrite() {
		final Element comp1 = getElement( comp2.class, 2 );
		final Match match1 = new Match( 0.9d, comp1 );

		final Element comp2 = getElement( comp2.class, 2 );
		final Match match2 = new Match( 0.9d, comp2 );

		final TreeSet<Match> bestMatches = new TreeSet<>();
		bestMatches.add( match1 );
		bestMatches.add( match2 );

		Assertions.assertThat( bestMatches.size() ).isEqualTo( 2 );
	}

	private Element getElement( final Class<?> clazz, final int cnt ) {
		return Element.create( "id", mock( Element.class ),
				IdentifyingAttributes.create( Path.path( new PathElement( clazz.getSimpleName(), 1 ) ), clazz ),
				toAttributes( "color", "green" ) );
	}

	private class comp1 extends Element {
		private static final long serialVersionUID = 1L;
	};

	private class comp2 extends Element {
		private static final long serialVersionUID = 1L;
	};
}
