package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class RecheckOptionsTest {

	@Test
	void should_reuse_file_namer_strategy_for_suite_name() throws Exception {
		final FileNamerStrategy fileNamerStrategy = mock( FileNamerStrategy.class );

		final RecheckOptions cut = RecheckOptions.builder() //
				.fileNamerStrategy( fileNamerStrategy ) //
				.build();

		assertThat( cut.getSuiteName() ).isEqualTo( fileNamerStrategy.getTestClassName() );
	}

	@Test
	void should_use_suite_name_if_set() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.suiteName( "bar" ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( "bar" );
	}

}
