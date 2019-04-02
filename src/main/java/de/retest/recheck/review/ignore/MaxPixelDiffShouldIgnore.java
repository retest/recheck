package de.retest.recheck.review.ignore;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaxPixelDiffShouldIgnore implements ShouldIgnore {

	private static final String PIXEL = "px";

	private final double maxPixelDiff;

	public MaxPixelDiffShouldIgnore( final double maxPixelDiff ) {
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
			final double expectedDouble = Double.parseDouble( clean( expected ) );
			final double actualDouble = Double.parseDouble( clean( actual ) );
			return Math.abs( expectedDouble - actualDouble ) <= maxPixelDiff;
		} catch ( final NumberFormatException e ) {
			log.error( "Could not parse {} and {} for max pixel diff.", expected, actual, e );
			return false;
		}
	}

	private static String clean( final String str ) {
		return str.replace( PIXEL, "" ).replace( ",", "." );
	}

	@Override
	public String toString() {
		return String.format( MaxPixelDiffShouldIgnoreLoader.FORMAT, maxPixelDiff );
	}

	public static class MaxPixelDiffShouldIgnoreLoader extends RegexLoader<MaxPixelDiffShouldIgnore> {

		private static final String KEY = "maxPixelDiff=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(\\d+(\\.\\d+)?)" );

		public MaxPixelDiffShouldIgnoreLoader() {
			super( REGEX );
		}

		@Override
		protected MaxPixelDiffShouldIgnore load( final MatchResult regex ) {
			final double maxPixelDiff = Double.parseDouble( regex.group( 1 ) );
			return new MaxPixelDiffShouldIgnore( maxPixelDiff );
		}
	}
}
