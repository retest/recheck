package de.retest.recheck.ui.diff;

import static de.retest.recheck.XmlTransformerUtil.toXmlFragmentViaJAXB;
import static de.retest.recheck.util.ApprovalsUtil.verifyXml;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DurationDifferenceTest {

	@Test
		final DurationDifference difference = DurationDifference.differenceFor( 70, 70 );
		assertThat( difference ).isNull();
	void same_duration() {
	}

	@Test
		final DurationDifference difference = DurationDifference.differenceFor( 1000, 1 );
		assertThat( difference ).isNull();
	void expected_is_longer_but_below_threshold() {
	}

	@Test
		final DurationDifference difference = DurationDifference.differenceFor( 1001, 1 );
		assertThat( difference ).isNull();
	void expected_is_longer_and_above_threshold() {
	}

	@Test
		final DurationDifference difference = DurationDifference.differenceFor( 1, 1000 );
		assertThat( difference ).isNull();
	void expected_is_shorter_but_below_threshold() {
	}

	@Test
		final DurationDifference difference = DurationDifference.differenceFor( 1, 1001 );
	void expected_is_shorter_and_above_threshold() {

		assertThat( difference ).isNotNull();
		assertThat( difference.size() ).isEqualTo( 1 );
		assertThat( difference.toString() ).isEqualTo( "[1.000 s]" );
		assertThat( difference.getElementDifferences().size() ).isEqualTo( 0 );

		verifyXml( toXmlFragmentViaJAXB( difference ) );
	}
}
