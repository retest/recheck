package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ValueRegexFilterTest {

	final ValueRegexFilter dateFilter = new ValueRegexFilter( "\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d" );
	final ValueRegexFilter buildNrFilter = new ValueRegexFilter( "\\d{4}" );
	final ValueRegexFilter yesNoFilter = new ValueRegexFilter( "(yes|no|perhaps)" );

	@Test
	void should_filter_matching_values() {
		assertThat( dateFilter.matches( mock( Element.class ), toDiff( "12.12.2019", "20.12.2019" ) ) ).isTrue();
		assertThat( buildNrFilter.matches( mock( Element.class ), toDiff( "3423", "3424" ) ) ).isTrue();
		assertThat( yesNoFilter.matches( mock( Element.class ), toDiff( "yes", "no" ) ) ).isTrue();
	}

	@Test
	void should_not_filter_non_matching_values() {
		assertThat( dateFilter.matches( mock( Element.class ), toDiff( "oh boy", "Hello!" ) ) ).isFalse();
		assertThat( buildNrFilter.matches( mock( Element.class ), toDiff( "3423", "sfsdgtse" ) ) ).isFalse();
		assertThat( yesNoFilter.matches( mock( Element.class ), toDiff( "no", "No way!" ) ) ).isFalse();
	}

	public AttributeDifference toDiff( final String expected, final String actual ) {
		return new AttributeDifference( "text", expected, actual );
	}

}
