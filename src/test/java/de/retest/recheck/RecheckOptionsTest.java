package de.retest.recheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import de.retest.recheck.RecheckOptions.RecheckOptionsBuilder;
import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.PersistentFilter;
import de.retest.recheck.persistence.ClassAndMethodBasedShortNamingStrategy;
import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.persistence.NamingStrategy;
import de.retest.recheck.persistence.ProjectLayout;

class RecheckOptionsTest {

	@Test
	void legacy_FileNamerStrategy_should_trump_for_suite_name() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.fileNamerStrategy( new MavenConformFileNamerStrategy() ) //
				.build();
		assertThat( cut.getNamingStrategy().getSuiteName() ).isEqualTo( getClass().getName() );
	}

	@Test
	void should_use_NamingStrategy_for_suite_name() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.namingStrategy( new ClassAndMethodBasedShortNamingStrategy() ).build();
		assertThat( cut.getNamingStrategy().getSuiteName() ).isEqualTo( getClass().getSimpleName() );
	}

	@Test
	void should_use_suite_name_if_set() throws Exception {
		final RecheckOptions cut = RecheckOptions.builder() //
				.suiteName( "bar" ) //
				.build();
		assertThat( cut.getNamingStrategy().getSuiteName() ).isEqualTo( "bar" );
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
		assertThat( ((PersistentFilter) filters.get( 0 )).getFilter().toString() )
				.isEqualTo( "# Style attributes filter file for recheck." );
	}

	@Test
	void setFilter_should_replace_existing() {
		final RecheckOptions cut = RecheckOptions.builder() //
				.setIgnore( "style-attributes.filter" ) //
				.build();
		assertThat( cut.getFilter() ).isInstanceOf( CompoundFilter.class );
		assertThat(
				((PersistentFilter) ((CompoundFilter) cut.getFilter()).getFilters().get( 0 )).getFilter().toString() )
						.isEqualTo( "# Style attributes filter file for recheck." );
	}

	@Test
	void suiteName_should_always_overwrite_the_naming_strategy() {
		final RecheckOptionsBuilder builder = RecheckOptions.builder().suiteName( "name" );
		assertThat( builder.build().getNamingStrategy().getSuiteName() ).isEqualTo( "name" );
	}

	@ParameterizedTest
	@MethodSource( "suiteNamesFileNameStrategy" )
	void suiteName_should_always_overwrite_the_naming_strategy_of_file_namer_strategy(
			final RecheckOptionsBuilder builder ) {
		final RecheckOptions options = builder.build();
		assertThat( options.getNamingStrategy().getSuiteName() ).isEqualTo( "name" );
		verify( options.getFileNamerStrategy(), atLeastOnce() ).createFileNamer( "name" );
	}

	@ParameterizedTest
	@MethodSource( "suiteNamesProjectLayout" )
	void suiteName_should_always_overwrite_the_naming_strategy_of_project_layout(
			final RecheckOptionsBuilder builder ) {
		final RecheckOptions options = builder.build();
		assertThat( options.getNamingStrategy().getSuiteName() ).isEqualTo( "name" );
		verify( options.getProjectLayout(), atLeastOnce() ).getSuiteFolder( "name" );
	}

	static Stream<RecheckOptionsBuilder> suiteNamesFileNameStrategy() {
		final FileNamer fileNamer = mock( FileNamer.class );
		when( fileNamer.getFile( any() ) ).thenReturn( new File( "src/test/resources/retest/recheck/name" ) );

		final FileNamerStrategy fileNamerStrategy = mock( FileNamerStrategy.class );
		when( fileNamerStrategy.getTestClassName() ).thenReturn( "suite" );
		when( fileNamerStrategy.getTestMethodName() ).thenReturn( "test" );
		when( fileNamerStrategy.createFileNamer( "name" ) ).thenReturn( fileNamer );

		return Stream.of( //
				RecheckOptions.builder() // name before file
						.suiteName( "name" ) //
						.fileNamerStrategy( fileNamerStrategy ), //
				RecheckOptions.builder() // name after file
						.fileNamerStrategy( fileNamerStrategy ) //
						.suiteName( "name" ) //
		);
	}

	static Stream<RecheckOptionsBuilder> suiteNamesProjectLayout() {
		final NamingStrategy namingStrategy = mock( NamingStrategy.class );
		when( namingStrategy.getSuiteName() ).thenReturn( "suite" );
		when( namingStrategy.getTestName() ).thenReturn( "test" );
		final ProjectLayout projectLayout = mock( ProjectLayout.class );
		when( projectLayout.getSuiteFolder( "name" ) ).thenReturn( new File( "name" ).toPath() );

		return Stream.of( //
				RecheckOptions.builder() // name before naming
						.suiteName( "name" ) //
						.namingStrategy( namingStrategy ) //
						.projectLayout( projectLayout ), //
				RecheckOptions.builder() // name after naming
						.namingStrategy( namingStrategy ) //
						.projectLayout( projectLayout )//
						.suiteName( "name" ) //
		);
	}

	@Test
	@SetSystemProperty( key = RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY, value = "true" )
	void build_with_rehub_not_set_should_read_property_true() {
		final RecheckOptions cut = RecheckOptions.builder().build();
		assertThat( cut.isReportUploadEnabled() ).isTrue();
	}

	@Test
	@SetSystemProperty( key = RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY, value = "false" )
	void build_with_rehub_not_set_should_read_property_false() {
		final RecheckOptions cut = RecheckOptions.builder().build();
		assertThat( cut.isReportUploadEnabled() ).isFalse();
	}

	@ParameterizedTest
	@ValueSource( strings = { "true", "false" } )
	@ClearSystemProperty( key = RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY )
	void build_with_rehub_enabled_should_overwrite_property_regardless_of_its_value( String global ) {
		System.setProperty( RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY, global );
		final RecheckOptions cut = RecheckOptions.builder() //
				.enableReportUpload() //
				.build();
		assertThat( cut.isReportUploadEnabled() ).isTrue();
	}

	@ParameterizedTest
	@ValueSource( strings = { "true", "false" } )
	@ClearSystemProperty( key = RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY )
	void build_with_rehub_disabled_should_overwrite_property_regardless_of_its_value( String global ) {
		System.setProperty( RecheckProperties.REHUB_REPORT_UPLOAD_ENABLED_PROPERTY_KEY, global );
		final RecheckOptions cut = RecheckOptions.builder() //
				.disableReportUpload() //
				.build();
		assertThat( cut.isReportUploadEnabled() ).isFalse();
	}
}
