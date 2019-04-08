package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ElementShouldIgnore implements Filter {

	private final Matcher<Element> matcher;

	public ElementShouldIgnore( final Matcher<Element> matcher ) {
		this.matcher = matcher;
	}

	@Override
	public boolean filterElement( final Element element ) {
		return matcher.test( element );
	}

	@Override
	public boolean filterAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		return false;
	}

	@Override
	public String toString() {
		return String.format( ElementShouldIgnoreLoader.FORMAT, matcher.toString() );
	}

	public static class ElementShouldIgnoreLoader extends RegexLoader<ElementShouldIgnore> {

		private static final String MATCHER = "matcher: ";

		private static final String FORMAT = MATCHER + "%s";
		private static final Pattern PREFIX = Pattern.compile( MATCHER + "(.+)" );

		public ElementShouldIgnoreLoader() {
			super( PREFIX );
		}

		@Override
		protected ElementShouldIgnore load( final MatchResult regex ) {
			final String matcher = regex.group( 1 );
			final Loader<Matcher> loader = Loaders.get( matcher );
			return new ElementShouldIgnore( loader.load( matcher ) );
		}
	}
}
