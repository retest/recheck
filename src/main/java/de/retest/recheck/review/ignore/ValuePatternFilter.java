package de.retest.recheck.review.ignore;

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

@Getter
public class ValuePatternFilter implements Filter {

	private final Pattern pattern;

	public ValuePatternFilter( final String regex ) {
		pattern = Pattern.compile( regex );
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		final Serializable actual = attributeDifference.getActual();

		if ( actual == null || !(actual instanceof String) ) {
			return false;
		}

		final Matcher matcher = pattern.matcher( (String) actual );

		if ( matcher.matches() ) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return String.format( ValuePatternFilterLoader.FORMAT, pattern );
	}

	public static class ValuePatternFilterLoader extends RegexLoader<ValuePatternFilter> {

		private static final String KEY = "value-pattern=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		public ValuePatternFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ValuePatternFilter> load( final MatchResult regex ) {
			final String value = regex.group( 1 );
			return Optional.of( new ValuePatternFilter( value ) );
		}
	}
}
