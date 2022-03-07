package de.retest.recheck.review.ignore;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class PixelDiffFilter implements Filter {

	private static final String PIXEL = "px";

	private static final Set<String> ignoredKeys = new HashSet<>( Arrays.asList( //
			"style", //
			"box-shadow" //
	) );

	/**
	 * Indicates whether {@link #pixelDiff} is specified as double ({@code true}) or integer ({@code false}). Although
	 * internally it is always treated as a double, this is important for serialization.
	 */
	private final String givenInput;
	private final double pixelDiff;

	public PixelDiffFilter( final String givenInput, final double pixelDiff ) {
		this.givenInput = givenInput.endsWith( PIXEL ) ? givenInput : givenInput + PIXEL;
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
		final String key = attributeDifference.getKey();

		if ( ignoredKeys.contains( key ) || expected == null || actual == null ) {
			return false;
		}

		if ( expected instanceof Rectangle ) {
			return checkRectangle( (Rectangle) expected, (Rectangle) actual );
		}

		if ( expected instanceof String ) {
			return checkString( key, (String) expected, (String) actual );
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

	private boolean checkString( final String key, final String expected, final String actual ) {
		if ( !expected.endsWith( PIXEL ) || !actual.endsWith( PIXEL ) ) {
			return false;
		}

		final String[] expectedParts = expected.split( " " );
		final String[] actualParts = actual.split( " " );
		if ( expectedParts.length != actualParts.length ) {
			return false;
		}

		for ( int i = 0; i < expectedParts.length; i++ ) {
			try {
				final double expectedDouble = Double.parseDouble( clean( expectedParts[i] ) );
				final double actualDouble = Double.parseDouble( clean( actualParts[i] ) );
				if ( Math.abs( expectedDouble - actualDouble ) > pixelDiff ) {
					return false;
				}
			} catch ( final NumberFormatException e ) {
				log.error( "Could not parse difference with key {}, expected '{}' and actual '{}' for pixel diff.", key,
						expected, actual );
				return false;
			}
		}
		return true;
	}

	private static String clean( final String str ) {
		return str.replace( PIXEL, "" ).replace( ",", "." );
	}

	@Override
	public String toString() {
		return String.format( PixelDiffFilterLoader.FORMAT, givenInput );
	}

	public static class PixelDiffFilterLoader extends RegexLoader<PixelDiffFilter> {

		private static final String KEY = "pixel-diff=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(\\d+(\\.\\d+)?)(px)?" );

		public PixelDiffFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<PixelDiffFilter> load( final MatchResult regex ) {
			final String value = regex.group( 1 );
			final double pixelDiff = Double.parseDouble( value );
			return Optional.of( new PixelDiffFilter( value, pixelDiff ) );
		}
	}
}
