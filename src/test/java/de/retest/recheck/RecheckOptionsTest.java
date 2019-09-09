package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.persistence.JUnitTestbasedShortNamingStrategy;

class RecheckOptionsTest {

	@Test
	void legacy_FileNamerStrategy_should_trump_for_suite_name() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.fileNamerStrategy( new MavenConformFileNamerStrategy() ) //
				.build();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void should_use_NamingStrategy_for_suite_name() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.namingStrategy( new JUnitTestbasedShortNamingStrategy() ).build();
		assertThat( cut.getSuiteName() ).isEqualTo( getClass().getSimpleName() );
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
		final RecheckOptions cut = RecheckOptions.builder() //
				.addIgnore( "style-attributes.filter" ) //
				.build();
		assertThat( cut.getFilter() ).isInstanceOf( CompoundFilter.class );
		final List<Filter> filters =
				((CompoundFilter) ((CompoundFilter) ((CompoundFilter) cut.getFilter()).getFilters().get( 0 ))
						.getFilters().get( 0 )).getFilters();
		assertThat( filters.get( 0 ).toString() ).isEqualTo( "# Style attributes filter file for recheck." );
	}

	@Test
	void setFilter_should_replace_existing() {
		final RecheckOptions cut = RecheckOptions.builder() //
				.setIgnore( "style-attributes.filter" ) //
				.build();
		assertThat( cut.getFilter() ).isInstanceOf( CompoundFilter.class );
		assertThat( ((CompoundFilter) cut.getFilter()).getFilters().get( 0 ).toString() )
				.isEqualTo( "# Style attributes filter file for recheck." );
	}
}
