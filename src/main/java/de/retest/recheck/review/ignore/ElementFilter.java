package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
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
		if ( matcher.test( element ) ) {
			return true;
		}
		final Element parent = element.getParent();
		return parent != null && matches( parent );

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
			final Matcher<Element> matcher = Loaders.elementMatcher().load( regex.group( 1 ) ).get();
			return new ElementFilter( matcher );
		}
	}
}
