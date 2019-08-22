package de.retest.recheck.review.ignore;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PixelDiffFilter implements Filter {

	private static final String PIXEL = "px";

	/**
	 * Indicates whether {@link #pixelDiff} is specified as float ({@code true}) or integer ({@code false}). Although
	 * internally it is always treated as a double, this is important for serialization.
	 */
	private final boolean specifiedAsFloat;
	private final double pixelDiff;

	public PixelDiffFilter( final boolean specifiedAsFloat, final double pixelDiff ) {
		this.specifiedAsFloat = specifiedAsFloat;
		this.pixelDiff = pixelDiff;
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
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
		final boolean filterX = Math.abs( expected.x - actual.x ) <= pixelDiff;
		final boolean filterY = Math.abs( expected.y - actual.y ) <= pixelDiff;
		final boolean filterHeight = Math.abs( expected.height - actual.height ) <= pixelDiff;
		final boolean filterWidth = Math.abs( expected.width - actual.width ) <= pixelDiff;
		return filterX && filterY && filterHeight && filterWidth;
	}

	private boolean checkString( final String expected, final String actual ) {
		if ( expected == null || actual == null ) {
			return false;
		}

		if ( !expected.endsWith( PIXEL ) || !actual.endsWith( PIXEL ) ) {
			return false;
		}

		try {
			final double expectedDouble = Double.parseDouble( clean( expected ) );
			final double actualDouble = Double.parseDouble( clean( actual ) );
			return Math.abs( expectedDouble - actualDouble ) <= pixelDiff;
		} catch ( final NumberFormatException e ) {
			log.error( "Could not parse {} and {} for pixel diff.", expected, actual, e );
			return false;
		}
	}

	private static String clean( final String str ) {
		return str.replace( PIXEL, "" ).replace( ",", "." );
	}

	@Override
	public String toString() {
		final String value = specifiedAsFloat ? Double.toString( pixelDiff ) : Integer.toString( (int) pixelDiff );
		return String.format( PixelDiffFilterLoader.FORMAT, value );
	}

	public static class PixelDiffFilterLoader extends RegexLoader<PixelDiffFilter> {

		private static final String KEY = "pixel-diff=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(\\d+(\\.\\d+)?)" );

		public PixelDiffFilterLoader() {
			super( REGEX );
		}

		@Override
		protected PixelDiffFilter load( final MatchResult regex ) {
			final String value = regex.group( 1 );
			final boolean specifiedAsFloat = value.contains( "." );
			final double pixelDiff = Double.parseDouble( value );
			return new PixelDiffFilter( specifiedAsFloat, pixelDiff );
		}
	}
}
