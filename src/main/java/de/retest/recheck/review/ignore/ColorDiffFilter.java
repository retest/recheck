package de.retest.recheck.review.ignore;

import static java.lang.Float.parseFloat;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.awt.Color;
import java.io.Serializable;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ColorDiffFilter implements Filter {

	private static final Pattern RGB = Pattern.compile( "rgb\\((\\d+),\\s?(\\d+),\\s?(\\d+)(,\\s?(\\d+))?\\)" );
	private static final double MAX_DISTANCE = 255;

	private final String givenInput;
	private final double colorDiff;

	public ColorDiffFilter( final String givenInput, final double colorDiff ) {
		this.givenInput = givenInput;
		this.colorDiff = colorDiff;
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		final Color expected = parse( attributeDifference.getExpected() );
		final Color actual = parse( attributeDifference.getActual() );

		if ( expected == null || actual == null ) {
			return false;
		}

		final double distance = calculateColorDistance( expected, actual ) * 100;

		return distance < colorDiff;
	}

	/**
	 * Return the color distance as a value in the range of [0.0, 1.0]. Note this is about perception: any one of the
	 * rgba values is reviewed in isolation, as a change from e.g. white to red or green to yellow already constitutes a
	 * 100% change in the perceived color.
	 */
	static double calculateColorDistance( final Color expected, final Color actual ) {
		final double r1 = expected.getRed();
		final double r2 = actual.getRed();
		final double g1 = expected.getGreen();
		final double g2 = actual.getGreen();
		final double b1 = expected.getBlue();
		final double b2 = actual.getBlue();
		final double a1 = expected.getAlpha();
		final double a2 = actual.getAlpha();

		final double distance = min( abs( r1 - r2 ) + abs( g1 - g2 ) + abs( b1 - b2 ) + abs( a1 - a2 ), 255 );
		return distance / MAX_DISTANCE;
	}

	static Color parse( final Serializable input ) {
		if ( input == null || !(input instanceof String) ) {
			return null;
		}
		final String color = (String) input;
		if ( color.startsWith( "#" ) ) {
			return Color.decode( color );
		}
		final Matcher matcher = RGB.matcher( color );
		if ( !matcher.find() ) {
			return null;
		}
		try {
			final String r = matcher.group( 1 );
			final String g = matcher.group( 2 );
			final String b = matcher.group( 3 );
			final String a = matcher.group( 5 );
			if ( a != null ) {
				return new Color( round( parseFloat( r ) ), round( parseFloat( g ) ), round( parseFloat( b ) ),
						round( parseFloat( a ) ) );
			}
			return new Color( round( parseFloat( r ) ), round( parseFloat( g ) ), round( parseFloat( b ) ) );
		} catch ( final Exception e ) {
			log.debug( "Error parsing input {} to color: ", input, e );
			return null;
		}
	}

	@Override
	public String toString() {
		return String.format( ColorDiffFilterLoader.FORMAT, givenInput );
	}

	public static class ColorDiffFilterLoader extends RegexLoader<ColorDiffFilter> {

		private static final String KEY = "color-diff=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(\\d+(\\.\\d+)?)(\\%)?" );

		public ColorDiffFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ColorDiffFilter> load( final MatchResult regex ) {
			final String value = regex.group( 1 );
			final double colorDiff = Double.parseDouble( value );
			return Optional.of( new ColorDiffFilter( value, colorDiff ) );
		}
	}
}
