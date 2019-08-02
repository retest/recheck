package de.retest.recheck;

import static de.retest.recheck.Properties.TEST_REPORT_FILE_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.retest.recheck.persistence.FileNamer;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.descriptors.RootElement;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.diff.LeafDifference;

class RecheckImplTest {

	@Test
	void using_strange_stepText_should_be_normalized() throws Exception {
		final FileNamerStrategy fileNamerStrategy = spy( new MavenConformFileNamerStrategy() );
		final RecheckOptions opts = RecheckOptions.builder() //
				.fileNamerStrategy( fileNamerStrategy ) //
				.build();
		final Recheck cut = new RecheckImpl( opts );
		final RecheckAdapter adapter = mock( RecheckAdapter.class );

		try {
			cut.check( mock( Object.class ), adapter, "!@#%$^&)te}{:|\\\":xt!(@*$" );
		} catch ( final Exception e ) {
			// Ignore Exceptions, fear AssertionErrors...
		}

		verify( fileNamerStrategy ).createFileNamer( eq( fileNamerStrategy.getTestClassName() ) );
		verify( fileNamerStrategy ).createFileNamer( endsWith( ".!@#_$^&)te}{_____xt!(@_$" ) );
	}

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

		final String goldenMasterName = "SomeTestClass/some-test.some-step.recheck";
		assertThatThrownBy( cut::capTest ) //
				.isExactlyInstanceOf( AssertionError.class ) //
				.hasMessageStartingWith( "'SomeTestClass':\n" + NoGoldenMasterActionReplayResult.MSG_LONG ) //
				.hasMessageEndingWith( goldenMasterName );

	}

	@Test
	void no_golden_master_error_message_should_be_formatted_properly() throws Exception {
		final ActionReplayResult actionReplayResult0 = mock( ActionReplayResult.class );
		when( actionReplayResult0.getGoldenMasterPath() ).thenReturn( "/gm/path/0" );

		final ActionReplayResult actionReplayResult1 = mock( ActionReplayResult.class );
		when( actionReplayResult1.getGoldenMasterPath() ).thenReturn( "/gm/path/1" );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );
		testReplayResult.addAction( actionReplayResult0 );
		testReplayResult.addAction( actionReplayResult1 );

		final RecheckImpl cut = new RecheckImpl();

		final String errorMessage = cut.getNoGoldenMasterErrorMessage( testReplayResult );
		assertThat( errorMessage ).isEqualTo( "'de.retest.recheck.RecheckImplTest':\n" //
				+ NoGoldenMasterActionReplayResult.MSG_LONG + "\n" //
				+ "/gm/path/0\n" //
				+ "/gm/path/1" );
	}

	@Test
	void inserted_and_deleted_differences_error_message_hould_be_formatted_properly() throws Exception {
		final IdentifyingAttributes identifyingAttributes = mock( IdentifyingAttributes.class );
		when( identifyingAttributes.getPath() ).thenReturn( "foo/bar/baz" );

		final Element absent = null;
		final Element present = mock( Element.class );
		when( present.getIdentifyingAttributes() ).thenReturn( identifyingAttributes );

		final InsertedDeletedElementDifference insertion =
				InsertedDeletedElementDifference.differenceFor( absent, present );
		final InsertedDeletedElementDifference deletion =
				InsertedDeletedElementDifference.differenceFor( present, absent );
		final LeafDifference ignore = mock( LeafDifference.class );

		// Use LinkedHashSet to guarantee order.
		final Set<LeafDifference> uniqueDifferences =
				new LinkedHashSet<>( Arrays.asList( insertion, deletion, ignore ) );

		final TestReplayResult testReplayResult = new TestReplayResult( "test-name", 1 );

		final RecheckImpl cut = new RecheckImpl();

		final String errorMessage = cut.getDifferencesErrorMessage( testReplayResult, uniqueDifferences );
		assertThat( errorMessage )
				.endsWith( "0 check(s) in 'de.retest.recheck.RecheckImplTest' found the following difference(s):\n" //
						+ "Test 'test-name' has 0 difference(s) in 0 state(s):\n" //
						+ "\tfoo/bar/baz was inserted!\n" //
						+ "\tfoo/bar/baz was deleted!\n" );
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
