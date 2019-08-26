package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;

class RecheckOptionsTest {

	@Test
	void should_reuse_file_namer_strategy_for_suite_name() throws Exception {
		final FileNamerStrategy fileNamerStrategy = spy( new MavenConformFileNamerStrategy() );
		when( fileNamerStrategy.getTestClassName() ).thenReturn( "foo" );
		final RecheckOptions cut = RecheckOptions.builder() //
				.fileNamerStrategy( fileNamerStrategy ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( "foo" );
	}

	@Test
	void should_use_suite_name_if_set() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.suiteName( "bar" ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( "bar" );
	}

	@Test
	void should_use_reportUploadEnabled() {
		final RecheckOptions cut = RecheckOptions.builder() //
				.enableReportUpload() //
				.build();
		assertThat( cut.isReportUploadEnabled() ).isEqualTo( true );
	}

	@Test
	void addFilter_should_add_filter_to_existing() {
		final Filter filter = mock( Filter.class );
		final RecheckOptions cut = RecheckOptions.builder() //
				.addFilter( filter ) //
				.build();
		assertThat( cut.getFilter() ).isInstanceOf( CompoundFilter.class );
		final CompoundFilter compoundFilter = (CompoundFilter) cut.getFilter();
		// added filter + global ignore + suite ignore
		assertThat( compoundFilter.getFilters() ).hasSize( 3 );
		assertThat( compoundFilter.getFilters() ).contains( filter );
	}

	@Test
	void setFilter_should_replace_existing() {
		final Filter filter = mock( Filter.class );
		final RecheckOptions cut = RecheckOptions.builder() //
				.setFilter( filter ) //
				.build();
		assertThat( cut.getFilter() ).isEqualTo( filter );
	}
}
