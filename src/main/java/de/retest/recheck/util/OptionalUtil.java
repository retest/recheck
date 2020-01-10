package de.retest.recheck.util;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OptionalUtil {

	private OptionalUtil() {}

	@SuppressWarnings( "unchecked" )
	public static <T> Stream<T> stream( final Optional<? extends T> o ) {
		return (Stream<T>) o.map( Stream::of ).orElseGet( Stream::empty );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> Optional<T> or​( final Optional<? extends T> o,
			final Supplier<? extends Optional<? extends T>> s ) {
		return (Optional<T>) (o.isPresent() ? o : s.get());
	}

}
