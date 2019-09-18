package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.JunitbasedShortNamingStrategy;
import de.retest.recheck.persistence.NamingStrategy;

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
				.namingStrategy( new JunitbasedShortNamingStrategy() ).build();
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

	@ParameterizedTest
	@MethodSource( "suiteNames" )
	void suiteName_should_always_overwrite_the_naming_strategy( final RecheckOptions options ) {
		assertThat( options.getSuiteName() ).isEqualTo( "name" );
		assertThat( options.getSuitePath() ).isEqualTo( new File( "src/test/resources/retest/recheck/name" ) );
	}

	static Stream<RecheckOptions> suiteNames() {
		final NamingStrategy namingStrategy = mock( NamingStrategy.class );
		when( namingStrategy.getSuiteName() ).thenReturn( "suite" );
		when( namingStrategy.getTestName() ).thenReturn( "test" );

		final FileNamer fileNamer = mock( FileNamer.class );
		when( fileNamer.getFile( any() ) ).thenReturn( new File( "src/test/resources/retest/recheck/name" ) );

		final FileNamerStrategy fileNamerStrategy = mock( FileNamerStrategy.class );
		when( fileNamerStrategy.getTestClassName() ).thenReturn( "suite" );
		when( fileNamerStrategy.getTestMethodName() ).thenReturn( "test" );
		when( fileNamerStrategy.createFileNamer( "name" ) ).thenReturn( fileNamer );

		return Stream.of( // 
				RecheckOptions.builder() // name only
						.suiteName( "name" ) //
						.build(), //
				RecheckOptions.builder() // name before naming
						.suiteName( "name" ) //
						.namingStrategy( namingStrategy ) //
						.build(), //
				RecheckOptions.builder() // name after naming
						.namingStrategy( namingStrategy ) //
						.suiteName( "name" ) //
						.build(), //
				RecheckOptions.builder() // name before file
						.suiteName( "name" ) //
						.fileNamerStrategy( fileNamerStrategy ) //
						.build(), //
				RecheckOptions.builder() // name after file
						.fileNamerStrategy( fileNamerStrategy ) //
						.suiteName( "name" ) //
						.build() //
		);
	}
}
