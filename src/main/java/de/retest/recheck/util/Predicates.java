package de.retest.recheck.util;

import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Predicates {
	private Predicates() {}

	public static <T> Predicate<T> catchExceptionAsFalse( final Predicate<T> unCatchedPredicate ) {
		return argument -> {
			try {
				return unCatchedPredicate.test( argument );
			} catch ( final Exception willBeIgnored ) {
				log.info( "Return false for ignored {}:", willBeIgnored.getClass(), willBeIgnored.getMessage() );
				return false;
			}
		};
	}

}
