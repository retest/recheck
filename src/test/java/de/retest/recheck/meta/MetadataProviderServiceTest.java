package de.retest.recheck.meta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class MetadataProviderServiceTest {

	@Test
	void retrieve_should_allow_for_duplicate_keys_and_should_always_prefer_local_over_global() throws Exception {
		final MetadataProvider global = mock( MetadataProvider.class );
		when( global.retrieve() ).thenReturn( Collections.singletonMap( "a", "b" ) );

		final MetadataProvider local = mock( MetadataProvider.class );
		when( local.retrieve() ).thenReturn( Collections.singletonMap( "a", "c" ) );

		final MetadataProviderService cut = new MetadataProviderService( global, local );

		assertThat( cut.retrieve() ) //
				.hasSize( 1 ) //
				.contains( Pair.of( "a", "c" ) );
	}
}
