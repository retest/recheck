package de.retest.recheck.review.ignore.io;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class InheritanceLoader<T> implements Loader<T> {

	private final List<Pair<Class<? extends T>, Loader<? extends T>>> registeredLoaders;

	@Override
	public Optional<T> load( final String line ) {
		return registeredLoaders.stream() //
				.<Loader<? extends T>> map( Pair::getRight ) //
				.map( loader -> loader.load( line ) ) //
				.filter( Optional::isPresent ) //
				.findFirst() //
				.map( Optional::get );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public String save( final T object ) {
		final Class<?> clazz = object.getClass();
		return registeredLoaders.stream() //
				.filter( pair -> pair.getLeft().isAssignableFrom( clazz ) ) //
				.findFirst() //
				.map( pair -> (Loader<T>) pair.getRight() ) // We must cast here, since we get a Loader<? extends T>
				.map( loader -> loader.save( object ) ) // otherwise this is not accepted by save( ? extends T )
				.orElseThrow( () -> noLoaderFor( object ) );
	}

	private UnsupportedOperationException noLoaderFor( final T object ) {
		return new UnsupportedOperationException( String.format( "Did not find a loader for %s.", object ) );
	}
}
