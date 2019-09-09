package de.retest.recheck.ignore;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class CacheFilterTest {

	@Test
	void matches_element_should_not_call_base_method_multiple_times() {
		final Element element = mock( Element.class );
		final Filter base = mock( Filter.class );

		final CacheFilter cut = new CacheFilter( base );

		cut.matches( element );
		cut.matches( element );

		verify( base ).matches( element );
		verifyNoMoreInteractions( base );
	}

	@Test
	void matches_difference_should_not_call_base_method_multiple_times() {
		final Element element = mock( Element.class );
		final AttributeDifference difference = mock( AttributeDifference.class );
		final Filter base = mock( Filter.class );

		final CacheFilter cut = new CacheFilter( base );

		cut.matches( element, difference );
		cut.matches( element, difference );

		verify( base ).matches( element, difference );
		verifyNoMoreInteractions( base );
	}
}
