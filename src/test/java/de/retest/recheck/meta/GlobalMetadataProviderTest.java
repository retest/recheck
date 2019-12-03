package de.retest.recheck.meta;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class GlobalMetadataProviderTest {

	@Test
	void retrieve_should_not_throw_an_exception() throws Exception {
		final MetadataProvider cut = new GlobalMetadataProvider();

		assertThatCode( cut::retrieve ).doesNotThrowAnyException();
	}
}
