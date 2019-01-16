package de.retest.recheck;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import de.retest.persistence.FileNamer;
import de.retest.ui.DefaultValueFinder;
import de.retest.ui.descriptors.IdentifyingAttributes;
import de.retest.ui.descriptors.RootElement;

@ExtendWith( TempDirectory.class )
class RecheckImplTest {

	@Test
	void test_class_name_should_be_default_result_file_name() throws Exception {
		final String execSuiteName = getClass().getName();
		final RecheckImpl cut = new RecheckImpl();
		final String resultFileName = cut.getResultFile().getName();
		assertThat( resultFileName ).isEqualTo( execSuiteName + ".result" );
	}

	@Test
	void exec_suite_name_should_be_used_for_result_file_name() throws Exception {
		final String execSuiteName = "FooBar";
		final RecheckImpl cut = new RecheckImpl( new MavenConformFileNamerStrategy(), execSuiteName );
		final String resultFileName = cut.getResultFile().getName();
		assertThat( resultFileName ).isEqualTo( execSuiteName + ".result" );
	}

	@Test
	void calling_check_without_startTest_should_work( @TempDir final Path root ) throws Exception {
		final RecheckImpl cut = new RecheckImpl( new WithinTempDirectoryFileNamerStrategy( root ) );
		cut.check( "String", new DummyStringRecheckAdapter(), "step" );
	}

	@Test
	void calling_with_no_GM_should_produce_better_error_msg( @TempDir final Path root ) throws Exception {
		final RecheckImpl cut = new RecheckImpl( new WithinTempDirectoryFileNamerStrategy( root ) );

		final RootElement rootElement = mock( RootElement.class );
		when( rootElement.getIdentifyingAttributes() ).thenReturn( mock( IdentifyingAttributes.class ) );

		final RecheckAdapter adapter = mock( RecheckAdapter.class );
		when( adapter.canCheck( any() ) ).thenReturn( true );
		when( adapter.convert( any() ) ).thenReturn( Collections.singleton( rootElement ) );

		cut.startTest( "Some test" );
		cut.check( "Some object to verify", adapter, "Some step" );

		assertThatThrownBy( cut::capTest ) //
				.isExactlyInstanceOf( AssertionError.class ) //
				.hasMessageStartingWith( "'SomeTestClass': \n" + //
						"No recheck file found. First time test was run? Created recheck file now, don't forget to commit...\n" );

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
			list.set( last, baseNames[last] + "." + extension );

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
