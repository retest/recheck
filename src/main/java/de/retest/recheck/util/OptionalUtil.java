package de.retest.recheck.util;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionalUtil {

	private OptionalUtil() {}

	public static <T> Stream<T> stream( final Optional<T> o ) {
		return o.map( Stream::of ).orElseGet( Stream::empty );
	}

}
