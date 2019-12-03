package de.retest.recheck.meta.global;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import de.retest.recheck.meta.MetadataProvider;

class OSMetadataProviderTest {

	@Test
	void retrieve_should_capture_all_required_metadata() throws Exception {
		final MetadataProvider cut = new OSMetadataProvider();

		final Map<String, String> metadata = cut.retrieve();

		assertThat( metadata ).hasSize( 3 );
		assertThat( metadata ).containsKey( OSMetadataProvider.OS_ARCH );
		assertThat( metadata ).containsKey( OSMetadataProvider.OS_NAME );
		assertThat( metadata ).containsKey( OSMetadataProvider.OS_VERSION );
	}
}
