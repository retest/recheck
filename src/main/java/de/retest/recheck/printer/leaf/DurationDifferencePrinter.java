package de.retest.recheck.printer.leaf;

import org.apache.commons.lang3.time.DurationFormatUtils;

import de.retest.recheck.printer.Printer;
import de.retest.recheck.ui.diff.DurationDifference;

public class DurationDifferencePrinter implements Printer<DurationDifference> {

	private static final String SECONDS_FORMAT = "ss.SSS";
	private static final String SECONDS_SUFFIX = " s";

	@Override
	public String toString( final DurationDifference difference, final String indent ) {
		final long actual = (long) difference.getActual();
		final long expected = (long) difference.getExpected();
		if ( actual < expected ) { // format can't handle negative values
			return indent + "-" + format( expected - actual );
		}
		return indent + format( actual - expected );
	}

	private static String format( final long duration ) {
		// TODO Unobfuscate DurationUtil and then use it here.
		return DurationFormatUtils.formatDuration( duration, SECONDS_FORMAT, false ) + SECONDS_SUFFIX;
	}
}
