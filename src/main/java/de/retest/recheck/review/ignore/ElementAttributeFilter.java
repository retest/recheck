package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;

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
	public boolean matches( final Element element, final String attributeKey ) {
		return matcher.test( element ) && key.equals( attributeKey );
	}

	@Override
	public String toString() {
		return String.format( ElementAttributeFilterLoader.FORMAT, matcher.toString(), key );
	}

	public static class ElementAttributeFilterLoader extends RegexLoader<ElementAttributeFilter> {

		static final String MATCHER = "matcher: ";
		static final String KEY = "attribute=";

		private static final String FORMAT = MATCHER + "%s, " + KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( MATCHER + "(.+), " + KEY + "(.+)" );

		public ElementAttributeFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementAttributeFilter> load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final String key = regex.group( 2 ).trim();
			return Loaders.elementMatcher().load( matcher ) //
					.map( match -> new ElementAttributeFilter( match, key ) );
		}
	}

	// TODO Remove again after it was sufficiently long in the project
	// for all .filter and recheck.ignore files to be migrated
	public static class LegacyElementAttributeFilterLoader extends RegexLoader<ElementAttributeFilter> {

		static final String KEY = "attribute: ";

		private static final Pattern REGEX =
				Pattern.compile( ElementAttributeFilterLoader.MATCHER + "(.+), " + KEY + "(.+)" );

		public LegacyElementAttributeFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementAttributeFilter> load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final String key = regex.group( 2 );
			return Loaders.elementMatcher().load( matcher ) //
					.map( match -> new ElementAttributeFilter( match, key ) );
		}
	}
}
