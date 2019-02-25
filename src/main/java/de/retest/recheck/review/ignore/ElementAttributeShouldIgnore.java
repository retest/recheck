package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ElementAttributeShouldIgnore implements ShouldIgnore {

	private final Matcher<Element> matcher;
	private final String key;

	public ElementAttributeShouldIgnore( final Matcher<Element> matcher, final String key ) {
		this.matcher = matcher;
		this.key = key;
	}

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		return matcher.test( element ) && key.equals( attributeDifference.getKey() );
	}

	public static class ElementAttributeShouldIgnoreLoader extends RegexLoader<ElementAttributeShouldIgnore> {

		private static final String MATCHER = "matcher: ";
		private static final String KEY = "key: ";

		private static final String FORMAT = MATCHER + "%s, " + KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( MATCHER + "(.+), " + KEY + "(.+)" );

		public ElementAttributeShouldIgnoreLoader() {
			super( REGEX );
		}

		@Override
		protected ElementAttributeShouldIgnore load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final Loader<Matcher> loader = Loaders.get( ElementRetestIdMatcher.class );
			final String key = regex.group( 2 );
			return new ElementAttributeShouldIgnore( loader.load( matcher ), key );
		}

		@Override
		public String save( final ElementAttributeShouldIgnore ignore ) {
			final Loader<Matcher> loader = Loaders.get( ElementRetestIdMatcher.class );
			return String.format( FORMAT, loader.save( ignore.matcher ), ignore.key );
		}
	}
}
