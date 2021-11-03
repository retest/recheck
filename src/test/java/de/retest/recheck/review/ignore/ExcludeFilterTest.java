package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

class ExcludeFilterTest {

	Filter cut;

	Element trueElement;
	Element falseElement;

	@BeforeEach
	void setUp() {
		trueElement = mock( Element.class );
		falseElement = mock( Element.class );

		final Filter delegate = mock( Filter.class );
		when( delegate.matches( trueElement ) ).thenReturn( true );
		when( delegate.matches( eq( trueElement ), anyString() ) ).thenReturn( true );
		when( delegate.matches( eq( trueElement ), any( AttributeDifference.class ) ) ).thenReturn( true );
		when( delegate.matches( eq( trueElement ), any( Filter.ChangeType.class ) ) ).thenReturn( true );

		cut = new ExcludeFilter( delegate );
	}

	@Test
	void matches_should_always_be_inverted() {
		assertThat( cut.matches( trueElement ) ).isFalse();
		assertThat( cut.matches( falseElement ) ).isTrue();

		assertThat( cut.matches( trueElement, "a" ) ).isFalse();
		assertThat( cut.matches( falseElement, "a" ) ).isTrue();

		assertThat( cut.matches( trueElement, mock( AttributeDifference.class ) ) ).isFalse();
		assertThat( cut.matches( falseElement, mock( AttributeDifference.class ) ) ).isTrue();

		assertThat( cut.matches( trueElement, Filter.ChangeType.CHANGED ) ).isFalse();
		assertThat( cut.matches( falseElement, Filter.ChangeType.CHANGED ) ).isTrue();
	}

	@Nested
	static class FilterLoaderTest {

		Loader<Filter> delegate;
		Loader<ExcludeFilter> cut;

		@BeforeEach
		void setUp() {
			delegate = mock( Loader.class );

			cut = new ExcludeFilter.FilterLoader( delegate );
		}

		@Test
		void load_should_fail_if_incomplete() {
			assertThat( cut.load( "exclude" ) ).isEmpty();
			assertThat( cut.load( "exclude(" ) ).isEmpty();
			assertThat( cut.load( "exclude(abc), exclude(" ) ).isEmpty();
			assertThat( cut.load( "exclude(abc, exclude(cde)" ) ).isEmpty();
		}

		@Test
		void load_should_fail_if_empty() {
			assertThat( cut.load( "exclude()" ) ).isEmpty();
		}

		@Test
		void load_should_load_single() {
			final Filter abc = mock( Filter.class );

			when( delegate.load( "abc" ) ).thenReturn( Optional.of( abc ) );

			assertThat( cut.load( "exclude(abc)" ) ).isPresent();
			verify( delegate, only() ).load( "abc" );
		}

		@Test
		void load_should_load_double() {
			final Filter abc = mock( Filter.class );
			final Filter def = mock( Filter.class );

			when( delegate.load( "abc" ) ).thenReturn( Optional.of( abc ) );
			when( delegate.load( "def" ) ).thenReturn( Optional.of( def ) );

			assertThat( cut.load( "exclude(abc), exclude(def)" ) ).isPresent();
			verify( delegate ).load( "abc" );
			verify( delegate ).load( "def" );
			verifyNoMoreInteractions( delegate );
		}

		@Test
		void load_should_load_triple() {
			final Filter abc = mock( Filter.class );
			final Filter def = mock( Filter.class );
			final Filter ghi = mock( Filter.class );

			when( delegate.load( "abc" ) ).thenReturn( Optional.of( abc ) );
			when( delegate.load( "def" ) ).thenReturn( Optional.of( def ) );
			when( delegate.load( "ghi" ) ).thenReturn( Optional.of( ghi ) );

			assertThat( cut.load( "exclude(abc), exclude(def), exclude(ghi)" ) ).isPresent();
			verify( delegate ).load( "abc" );
			verify( delegate ).load( "def" );
			verify( delegate ).load( "ghi" );
			verifyNoMoreInteractions( delegate );
		}

		@Test
		void load_should_forward_spaces_to_delegate() {
			final Filter wrapped = mock( Filter.class );

			when( delegate.load( " foo" ) ).thenReturn( Optional.of( wrapped ) );
			assertThat( cut.load( "exclude( foo)" ) ).isPresent();
			assertThat( cut.load( "exclude( foo )" ) ).isEmpty();

			when( delegate.load( "foo " ) ).thenReturn( Optional.of( wrapped ) );
			assertThat( cut.load( "exclude(foo )" ) ).isPresent();
			assertThat( cut.load( "exclude( foo )" ) ).isEmpty();

			when( delegate.load( " foo " ) ).thenReturn( Optional.of( wrapped ) );
			assertThat( cut.load( "exclude( foo )" ) ).isPresent();
		}

		@Test
		void load_should_fail_if_delegate_fails_to_load() {
			when( delegate.load( anyString() ) ).thenReturn( Optional.empty() );

			assertThat( cut.load( "exclude(foo)" ) ).isEmpty();
			verify( delegate ).load( "foo" );
		}

		@Test
		void load_should_parse_nested_excludes() {
			when( delegate.load( "abc, exclude(def)" ) ).thenReturn( Optional.of( mock( Filter.class ) ) );
			when( delegate.load( "ghi" ) ).thenReturn( Optional.of( mock( Filter.class ) ) );

			assertThat( cut.load( "exclude(abc, exclude(def))" ) ).isPresent();
			assertThat( cut.load( "exclude(abc, exclude(def)), exclude(ghi)" ) ).isPresent();
		}

		@Test
		void load_should_abort_if_multiple_child_fails() {
			final Filter abc = mock( Filter.class );
			when( delegate.load( "abc" ) ).thenReturn( Optional.of( abc ) );

			assertThat( cut.load( "exclude(abc), exclude(def)" ) ).isEmpty();
			assertThat( cut.load( "exclude(def), exclude(abc)" ) ).isEmpty();
		}

		@Test
		void save_should_delegate() {
			when( delegate.save( any( Filter.class ) ) ).thenReturn( "foo" );

			final Filter filter = mock( Filter.class );
			final ExcludeFilter negated = new ExcludeFilter( filter );

			assertThat( cut.save( negated ) ).isEqualTo( "exclude(foo)" );
			verify( delegate ).save( filter );
		}

		@Test
		void save_should_wrap_each_filter() {
			when( delegate.save( any( Filter.class ) ) ).thenReturn( "foo" );

			final Filter filter = mock( Filter.class );
			final ExcludeFilter negated = new ExcludeFilter( new CompoundFilter( filter, filter ) );

			assertThat( cut.save( negated ) ).isEqualTo( "exclude(foo), exclude(foo)" );
			verify( delegate, times( 2 ) ).save( filter );
		}
	}
}
