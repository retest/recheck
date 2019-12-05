package de.retest.recheck.meta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class MultiMetadataProviderTest {

	@Test
	void retrieve_should_concatenate_data() throws Exception {
		final MetadataProvider first = mock( MetadataProvider.class );
		when( first.retrieve() ).thenReturn( Collections.singletonMap( "a", "b" ) );

		final MetadataProvider second = mock( MetadataProvider.class );
		when( second.retrieve() ).thenReturn( Collections.singletonMap( "b", "c" ) );

		final MetadataProvider cut = MultiMetadataProvider.of( first, second );

		assertThat( cut.retrieve() ) //
				.hasSize( 2 ) //
				.contains( Pair.of( "a", "b" ) ) //
				.contains( Pair.of( "b", "c" ) );
	}

	@Test
	void retrieve_should_not_allow_for_null_values() throws Exception {
		final MetadataProvider first = mock( MetadataProvider.class );
		when( first.retrieve() ).thenReturn( Collections.singletonMap( "a", null ) );

		final MetadataProvider cut = MultiMetadataProvider.of( first );

		assertThatThrownBy( cut::retrieve ) //
				.isInstanceOf( NullPointerException.class );
	}

	@Test
	void retrieve_should_take_last_value_encountered() throws Exception {
		final MetadataProvider first = mock( MetadataProvider.class );
		when( first.retrieve() ).thenReturn( Collections.singletonMap( "a", "b" ) );

		final MetadataProvider second = mock( MetadataProvider.class );
		when( second.retrieve() ).thenReturn( Collections.singletonMap( "a", "c" ) );

		final MetadataProvider firstSecond = MultiMetadataProvider.of( first, second );
		final MetadataProvider secondFirst = MultiMetadataProvider.of( second, first );

		assertThat( firstSecond.retrieve() ) //
				.hasSize( 1 ) //
				.contains( Pair.of( "a", "c" ) );

		assertThat( secondFirst.retrieve() ) //
				.hasSize( 1 ) //
				.contains( Pair.of( "a", "b" ) );
	}
}
