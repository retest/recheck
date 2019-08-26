package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.RecheckIgnoreUtil;

@RunWith( PowerMockRunner.class )
@PrepareForTest( RecheckIgnoreUtil.class )
public class RecheckOptionsTest {

	@Test
	public void should_reuse_file_namer_strategy_for_suite_name() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder().build();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	public void should_use_suite_name_if_set() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.suiteName( "bar" ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( "bar" );
	}

	@Test
	public void should_use_reportUploadEnabled() {
		final RecheckOptions cut = RecheckOptions.builder() //
				.enableReportUpload() //
				.build();
		assertThat( cut.isReportUploadEnabled() ).isEqualTo( true );
	}

	@Test
	public void addFilter_should_add_filter_to_existing() {
		final Filter filter = mock( Filter.class );
		final RecheckOptions cut = RecheckOptions.builder() //
				.addFilter( filter ) //
				.build();
		assertThat( cut.getFilter() ).isInstanceOf( CompoundFilter.class );
		assertThat( ((CompoundFilter) cut.getFilter()).getFilters() ).contains( filter );
	}

	@Test
	public void filter_should_replace_existing() {
		final Filter filter = mock( Filter.class );
		final RecheckOptions cut = RecheckOptions.builder() //
				.setFilter( filter ) //
				.build();
		assertThat( cut.getFilter() ).isEqualTo( filter );
	}
}
