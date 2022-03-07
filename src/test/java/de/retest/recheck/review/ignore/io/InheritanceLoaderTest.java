package de.retest.recheck.review.ignore.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InheritanceLoaderTest {

	Loader<A> cut;

	final A a = new A();
	final B b = new B();
	final C c = new C();
	final Z z = new Z();

	@BeforeEach
	@SuppressWarnings( "unchecked" )
	void setUp() {
		final Loader<A> aLoader = mock( Loader.class );
		when( aLoader.load( "a" ) ).thenReturn( Optional.of( a ) );
		when( aLoader.save( a ) ).thenReturn( "a" );

		final Loader<B> bLoader = mock( Loader.class );
		when( bLoader.load( "b" ) ).thenReturn( Optional.of( b ) );
		when( bLoader.save( b ) ).thenReturn( "b" );

		final Loader<C> cLoader = mock( Loader.class );
		when( cLoader.load( "c" ) ).thenReturn( Optional.of( c ) );
		when( cLoader.save( c ) ).thenReturn( "c" );

		cut = new InheritanceLoader<>( Arrays.asList( //
				Pair.of( B.class, bLoader ), //	
				Pair.of( C.class, cLoader ), // 	
				Pair.of( A.class, aLoader ) //
		) );
	}

	@Test
	void load_should_parse_correct_strings() throws Exception {
		assertThat( cut.load( "a" ) ).hasValue( a );
		assertThat( cut.load( "b" ) ).hasValue( b );
		assertThat( cut.load( "c" ) ).hasValue( c );
		assertThat( cut.load( "d" ) ).isNotPresent();
	}

	@Test
	void load_stream_should_parse_correct_strings() throws Exception {
		assertThat( cut.load( Stream.of( "a", "b", "c", "d" ) ) ).contains( a, b, c );
		assertThat( cut.load( Stream.of( "a", "a", "b", "c" ) ) ).contains( a, a, b, c );
	}

	@Test
	void save_should_produce_correct_strings() throws Exception {
		assertThat( cut.save( a ) ).isEqualTo( "a" );
		assertThat( cut.save( b ) ).isEqualTo( "b" );
		assertThat( cut.save( c ) ).isEqualTo( "c" );
	}

	@Test
	void save_stream_should_produce_correct_strings() throws Exception {
		assertThat( cut.save( Stream.of( a, b, c ) ) ).contains( "a", "b", "c" );
		assertThat( cut.save( Stream.of( a, a, b, c ) ) ).contains( "a", "a", "b", "c" );
	}

	@Test
	void save_with_unsupported_loader_should_produce_exception() throws Exception {
		assertThatThrownBy( () -> cut.save( z ) ) //
				.isInstanceOf( UnsupportedOperationException.class ) //
				.hasMessage( String.format( "Did not find a loader for %s.", z ) );
	}

	@Test
	void save_stream_with_unsupported_loader_should_produce_exception() throws Exception {
		// We need a finalizer method on the stream here, so that the stream actually evaluates and throws an exception
		assertThatThrownBy( () -> cut.save( Stream.of( a, z, b ) ).toArray() )
				.isInstanceOf( UnsupportedOperationException.class ) //
				.hasMessage( String.format( "Did not find a loader for %s.", z ) );
	}

	static class A {}

	static class B extends A {}

	static class C extends A {}

	static class Z extends A {}
}
