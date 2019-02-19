package de.retest.recheck.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

class GloballyIgnoredAttributesTest {

	@Test
	public void only_ignored_attribute_should_be_ignored() throws Exception {
		final GloballyIgnoredAttributes ignored =
				GloballyIgnoredAttributes.getTestInstance( Collections.singletonList( "text" ) );
		assertThat( ignored.shouldIgnoreAttribute( "text" ) ).isTrue();
		assertThat( ignored.shouldIgnoreAttribute( "color" ) ).isFalse();
	}
}
