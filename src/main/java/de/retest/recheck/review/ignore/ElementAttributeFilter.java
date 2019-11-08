package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ElementAttributeFilter implements Filter {

	private final Matcher<Element> matcher;
	private final String key;

	public ElementAttributeFilter( final Matcher<Element> matcher, final String key ) {
		this.matcher = matcher;
		this.key = key;
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return matcher.test( element ) && key.equals( attributeDifference.getKey() );
	}

	@Override
	public String toString() {
		return String.format( ElementAttributeFilterLoader.FORMAT, matcher.toString(), key );
	}

	public static class ElementAttributeFilterLoader extends RegexLoader<ElementAttributeFilter> {

		static final String MATCHER = "matcher: ";
		static final String KEY = "attribute: ";

		private static final String FORMAT = MATCHER + "%s, " + KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( MATCHER + "(.+), " + KEY + "(.+)" );

		public ElementAttributeFilterLoader() {
			super( REGEX );
		}

		@Override
		protected ElementAttributeFilter load( final MatchResult regex ) {
			final Matcher<Element> matcher = Loaders.elementMatcher().load( regex.group( 1 ) ).get();
			final String key = regex.group( 2 );
			return new ElementAttributeFilter( matcher, key );
		}
	}
}
