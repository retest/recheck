package de.retest.recheck.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TextAttributeTest {

	@Test
	void should_not_trim_the_value() {
		final String text = " foo ";
		final TextAttribute cut = new TextAttribute( "text", text );
		assertThat( cut.getValue() ).isEqualTo( text );
	}
}
