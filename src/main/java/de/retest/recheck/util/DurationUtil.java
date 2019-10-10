package de.retest.recheck.util;

import static java.lang.Long.getLong;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

public class DurationUtil {

	public static final String DURATION_DIFFERENCE_PROPERTY = "de.retest.difference.duration.value";
	public static final long DURATION_DIFFERENCE_DEFAULT = 1l;

	public static String durationToFormattedString( final long duration ) {
		final long posDuration = duration < 0 ? -duration : duration;
		final String sign = duration < 0 ? "-" : "";

		final long millis = posDuration % 1000l;
		final long secs = TimeUnit.MILLISECONDS.toSeconds( posDuration ) % 60l;
		final long mins = TimeUnit.MILLISECONDS.toMinutes( posDuration ) % 60l;
		final long hrs = TimeUnit.MILLISECONDS.toHours( posDuration );
		if ( hrs > 0 ) {
			return sign + String.format( "%d:%02d:%02d h", hrs, mins, secs );
		}
		if ( mins > 0 ) {
			return sign + String.format( "%d:%02d min", mins, secs );
		}
		return sign + String.format( "%d.%03d s", secs, millis );
	}

	public static boolean ignore( final long expected, final long actual ) {
		return ignore( Math.abs( actual - expected ) );
	}

	public static boolean ignore( final long duration ) {
		return duration < SECONDS.toMillis( getLong( DURATION_DIFFERENCE_PROPERTY, DURATION_DIFFERENCE_DEFAULT ) );
	}

}
