package de.retest.recheck.ignore;

import java.awt.Rectangle;
import java.io.Serializable;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IgnorePixelDiff implements ShouldIgnore {

	private static final String PIXEL = "px";

	private final double maxPixelDiff;

	public IgnorePixelDiff( final double maxPixelDiff ) {
		this.maxPixelDiff = maxPixelDiff;
	}

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		final Serializable expected = attributeDifference.getExpected();
		final Serializable actual = attributeDifference.getActual();

		if ( expected instanceof Rectangle ) {
			return checkRectangle( (Rectangle) expected, (Rectangle) actual );
		}

		if ( expected instanceof String ) {
			return checkString( (String) expected, (String) actual );
		}

		return false;
	}

	private boolean checkRectangle( final Rectangle expected, final Rectangle actual ) {
		final boolean ignoreX = Math.abs( expected.x - actual.x ) <= maxPixelDiff;
		final boolean ignoreY = Math.abs( expected.y - actual.y ) <= maxPixelDiff;
		final boolean ignoreHeight = Math.abs( expected.height - actual.height ) <= maxPixelDiff;
		final boolean ignoreWidth = Math.abs( expected.width - actual.width ) <= maxPixelDiff;
		return ignoreX && ignoreY && ignoreHeight && ignoreWidth;
	}

	private boolean checkString( final String expected, final String actual ) {
		if ( expected == null || actual == null ) {
			return false;
		}

		if ( !expected.endsWith( PIXEL ) || !actual.endsWith( PIXEL ) ) {
			return false;
		}

		try {
			final double expectedDouble = Double.valueOf( clean( expected ) );
			final double actualDouble = Double.valueOf( clean( actual ) );
			return Math.abs( expectedDouble - actualDouble ) <= maxPixelDiff;
		} catch ( final NumberFormatException e ) {
			log.error( "Could not parse {} and {} for max pixel diff.", expected, actual, e );
			return false;
		}
	}

	private static String clean( final String str ) {
		return str.replace( PIXEL, "" ).replace( ",", "." );
	}

}
