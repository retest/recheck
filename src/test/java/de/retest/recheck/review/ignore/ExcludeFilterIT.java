package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ExcludeFilterIT {

	@Test
	void load_should_handle_multiple_entries() {
		final Element element = mock( Element.class );

		final AttributeFilter.AttributeFilterLoader delegate = new AttributeFilter.AttributeFilterLoader();
		final ExcludeFilter.FilterLoader cut = new ExcludeFilter.FilterLoader( delegate );

		assertThat( cut.load( "exclude(attribute=text), exclude(attribute=font)" ) ).hasValueSatisfying( filter -> {
			assertThat( filter.matches( element, "class" ) ).isTrue();
			assertThat( filter.matches( element, difference( "class" ) ) ).isTrue();

			assertThat( filter.matches( element, "font" ) ).isFalse();
			assertThat( filter.matches( element, difference( "font" ) ) ).isFalse();

			assertThat( filter.matches( element, "text" ) ).isFalse();
			assertThat( filter.matches( element, difference( "text" ) ) ).isFalse();
		} );
	}

	private AttributeDifference difference( final String key ) {
		return new AttributeDifference( key, "expected", "actual" );
	}
}
