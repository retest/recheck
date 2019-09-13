package de.retest.recheck.util;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {

	private StreamUtil() {}

	public static <T> Stream<T> optionalToStream( final Optional<T> o ) {
		return o.map( Stream::of ).orElseGet( Stream::empty );
	}

}
