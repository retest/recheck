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
public class ValueRegexFilter implements Filter {

	private final Pattern pattern;

	public ValueRegexFilter( final String regex ) {
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

		return matcher.matches();
	}

	@Override
	public String toString() {
		return String.format( ValueRegexFilterLoader.FORMAT, pattern );
	}

	public static class ValueRegexFilterLoader extends RegexLoader<ValueRegexFilter> {

		private static final String KEY = "value-regex=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		public ValueRegexFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ValueRegexFilter> load( final MatchResult regex ) {
			final String value = regex.group( 1 );
			return Optional.of( new ValueRegexFilter( value ) );
		}
	}
}
