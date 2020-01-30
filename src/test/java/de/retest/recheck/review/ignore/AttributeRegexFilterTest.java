package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ui.descriptors.Element;

class AttributeRegexFilterTest {

	@Test
	void should_filter_if_regex_matches() {
		final Filter cut = new AttributeRegexFilter( "my-.*-key" );
		assertThat( cut.matches( mock( Element.class ), "my-somewhatspecial-key" ) ).isTrue();
	}

	@Test
	void should_not_filter_if_regex_doesnt_match() {
		final Filter cut = new AttributeRegexFilter( "my-.*-key" );
		assertThat( cut.matches( mock( Element.class ), "my-otherattribute" ) ).isFalse();
	}

}
