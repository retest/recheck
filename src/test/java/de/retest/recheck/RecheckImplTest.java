package de.retest.recheck;

import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;

class RecheckImplTest {

	@Test
	void test_class_name_should_be_default_result_file_name() throws Exception {
		final String suiteName = getClass().getName();
		final RecheckImpl cut = new RecheckImpl();
		final String resultFileName = cut.getResultFile().getName();
		assertThat( resultFileName ).isEqualTo( suiteName + TEST_REPORT_FILE_EXTENSION );
	}

	@Test
	void exec_suite_name_should_be_used_for_result_file_name() throws Exception {
		final String suiteName = "FooBar";
		final RecheckOptions opts = RecheckOptions.builder() //
				.suiteName( suiteName ) //
				.build();
		final RecheckImpl cut = new RecheckImpl( opts );
		final String resultFileName = cut.getResultFile().getName();
		assertThat( resultFileName ).isEqualTo( suiteName + TEST_REPORT_FILE_EXTENSION );
	}

	@Test
	void calling_check_without_startTest_should_work( @TempDir final Path root ) throws Exception {
		final RecheckOptions opts = RecheckOptions.builder() //
				.fileNamerStrategy( new WithinTempDirectoryFileNamerStrategy( root ) ) //
				.build();
		final RecheckImpl cut = new RecheckImpl( opts );
		cut.check( "String", new DummyStringRecheckAdapter(), "step" );
	}

	@Test
	void calling_with_no_GM_should_produce_better_error_msg( @TempDir final Path root ) throws Exception {
		final RecheckOptions opts = RecheckOptions.builder() //
				.fileNamerStrategy( new WithinTempDirectoryFileNamerStrategy( root ) ) //
				.build();
		final RecheckImpl cut = new RecheckImpl( opts );

		final RootElement rootElement = mock( RootElement.class );
		when( rootElement.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		final RecheckAdapter adapter = mock( RecheckAdapter.class );
		when( adapter.canCheck( any() ) ).thenReturn( true );
		when( adapter.convert( any() ) ).thenReturn( Collections.singleton( rootElement ) );

		cut.startTest( "some-test" );
		cut.check( "to-verify", adapter, "some-step" );

		final String goldenMasterName = "de.retest.recheck.RecheckImplTest/some-test.some-step.recheck";
		assertThatThrownBy( cut::capTest ) //
				.isExactlyInstanceOf( AssertionError.class ) //
				.hasMessageStartingWith(
						"'" + getClass().getName() + "':\n" + NoGoldenMasterActionReplayResult.MSG_LONG ) //
				.hasMessageEndingWith( goldenMasterName );

	}

	private static class DummyStringRecheckAdapter implements RecheckAdapter {

		@Override
		public DefaultValueFinder getDefaultValueFinder() {
			return null;
		}

		@Override
		public Set<RootElement> convert( final Object arg0 ) {
			final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
			final RootElement rootElement = mock( RootElement.class );
			when( rootElement.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );
			return Collections.singleton( rootElement );
		}

		@Override
		public boolean canCheck( final Object arg0 ) {
			return false;
		}
	}

	private static class WithinTempDirectoryFileNamerStrategy implements FileNamerStrategy {

		private final Path root;

		public WithinTempDirectoryFileNamerStrategy( final Path root ) throws IOException {
			this.root = root;
		}

		@Override
		public FileNamer createFileNamer( final String... baseNames ) {
			return new FileNamer() {

				@Override
				public File getFile( final String extension ) {
					return resolveRoot( baseNames, extension );
				}

				@Override
				public File getResultFile( final String extension ) {
					return resolveRoot( baseNames, extension );
				}
			};
		}

		private File resolveRoot( final String[] baseNames, final String extension ) {
			final int last = baseNames.length - 1;
			final List<String> list = new ArrayList<>( Arrays.asList( baseNames ) );
			list.set( last, baseNames[last] + extension );

			Path path = root;
			for ( final String sub : list ) {
				path = path.resolve( sub );
			}

			return path.toFile();
		}

		@Override
		public String getTestClassName() {
			return "SomeTestClass";
		}

		@Override
		public String getTestMethodName() {
			return "someTestMethod";
		}
	}
}
