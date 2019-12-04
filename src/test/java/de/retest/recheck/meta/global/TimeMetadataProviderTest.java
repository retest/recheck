package de.retest.recheck.meta.global;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import de.retest.recheck.meta.MetadataProvider;

class TimeMetadataProviderTest {

	@Test
	void retrieve_should_capture_all_required_metadata() throws Exception {
		final MetadataProvider cut = new TimeMetadataProvider();

		final Map<String, String> metadata = cut.retrieve();

		assertThat( metadata ).hasSize( 4 );
		assertThat( metadata ).containsKey( TimeMetadataProvider.TIME );
		assertThat( metadata ).containsKey( TimeMetadataProvider.DATE );
		assertThat( metadata ).containsKey( TimeMetadataProvider.OFFSET );
		assertThat( metadata ).containsKey( TimeMetadataProvider.ZONE );
	}

}
