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
		final double redDiff = abs( expected.getRed() - actual.getRed() );
		final double greenDiff = abs( expected.getGreen() - actual.getGreen() );
		final double blueDiff = abs( expected.getBlue() - actual.getBlue() );
		final double alphaDiff = abs( expected.getAlpha() - actual.getAlpha() );

		// min so we don't exceed max_distance
		final double distance = min( redDiff + greenDiff + blueDiff + alphaDiff, 255 );
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
			final String red = matcher.group( 1 );
			final String green = matcher.group( 2 );
			final String blue = matcher.group( 3 );
			final String alpha = matcher.group( 5 );
			if ( alpha != null ) {
				return new Color( round( parseFloat( red ) ), round( parseFloat( green ) ), round( parseFloat( blue ) ),
						round( parseFloat( alpha ) ) );
			}
			return new Color( round( parseFloat( red ) ), round( parseFloat( green ) ), round( parseFloat( blue ) ) );
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
