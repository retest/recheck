package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ElementAttributeRegexFilter implements Filter {

	private final Matcher<Element> matcher;
	private final Pattern attributePattern;

	public ElementAttributeRegexFilter( final Matcher<Element> matcher, final String attributeRegex ) {
		this.matcher = matcher;
		attributePattern = Pattern.compile( attributeRegex );
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return matcher.test( element ) && attributePattern.matcher( attributeDifference.getKey() ).matches();
	}

	@Override
	public String toString() {
		return String.format( ElementAttributeRegexFilterLoader.FORMAT, matcher.toString(), attributePattern );
	}

	public static class ElementAttributeRegexFilterLoader extends RegexLoader<ElementAttributeRegexFilter> {

		static final String MATCHER = "matcher: ";
		static final String KEY = "attribute-regex: ";

		private static final String FORMAT = MATCHER + "%s, " + KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( MATCHER + "(.+), " + KEY + "(.+)" );

		public ElementAttributeRegexFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementAttributeRegexFilter> load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final String key = regex.group( 2 );
			return Loaders.elementMatcher().load( matcher ) //
					.map( match -> new ElementAttributeRegexFilter( match, key ) );
		}
	}
}
