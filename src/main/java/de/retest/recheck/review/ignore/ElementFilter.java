package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;

public class ElementFilter implements Filter {

	private final Matcher<Element> matcher;

	public ElementFilter( final Matcher<Element> matcher ) {
		this.matcher = matcher;
	}

	@Override
	public boolean matches( final Element element ) {
		return matcher.test( element );
	}

	@Override
	public String toString() {
		return String.format( ElementFilterLoader.FORMAT, matcher.toString() );
	}

	public static class ElementFilterLoader extends RegexLoader<ElementFilter> {

		static final String MATCHER = "matcher: ";

		private static final String FORMAT = MATCHER + "%s";
		private static final Pattern PREFIX = Pattern.compile( MATCHER + "(.+)" );

		public ElementFilterLoader() {
			super( PREFIX );
		}

		@Override
		protected ElementFilter load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final Loader<Matcher> loader = Loaders.get( matcher );
			return new ElementFilter( loader.load( matcher ) );
		}
	}
}
