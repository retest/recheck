package de.retest.recheck.ui.diff;

import static de.retest.recheck.XmlTransformerUtil.toXmlFragmentViaJAXB;
import static de.retest.recheck.util.ApprovalsUtil.verifyXml;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.retest.recheck.util.DurationUtil;
import de.retest.recheck.util.junit.jupiter.SystemProperty;

@SystemProperty( key = DurationUtil.DURATION_DIFFERENCE_PROPERTY, value = "1" )
class DurationDifferenceTest {

	@Test
	void same_duration() {
		final DurationDifference cut = DurationDifference.differenceFor( 70, 70 );
		assertThat( cut ).isNull();
	}

	@Test
	void expected_is_longer_but_below_threshold() {
		final DurationDifference cut = DurationDifference.differenceFor( 1000, 1 );
		assertThat( cut ).isNull();
	}

	@Test
	void expected_is_shorter_but_below_threshold() {
		final DurationDifference cut = DurationDifference.differenceFor( 1, 1000 );
		assertThat( cut ).isNull();
	}

	@Test
	void expected_is_longer_and_above_threshold() {
		final DurationDifference cut = DurationDifference.differenceFor( 1001, 1 );

		assertThat( cut ).isNotNull();
		assertThat( cut.size() ).isEqualTo( 1 );
		assertThat( cut.toString() ).isEqualTo( "[-1.000 s]" );
		assertThat( cut.getElementDifferences().size() ).isEqualTo( 0 );

		verifyXml( toXmlFragmentViaJAXB( cut ) );
	}

	@Test
	void expected_is_shorter_and_above_threshold() {
		final DurationDifference cut = DurationDifference.differenceFor( 1, 1001 );

		assertThat( cut ).isNotNull();
		assertThat( cut.size() ).isEqualTo( 1 );
		assertThat( cut.toString() ).isEqualTo( "[1.000 s]" );
		assertThat( cut.getElementDifferences().size() ).isEqualTo( 0 );

		verifyXml( toXmlFragmentViaJAXB( cut ) );
	}
}
