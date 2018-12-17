package de.retest.recheck.printer;

@FunctionalInterface
public interface Printer<D> {

	default String toString( final D difference ) {
		return toString( difference, "" );
	}

	String toString( D difference, final String indent );
}
