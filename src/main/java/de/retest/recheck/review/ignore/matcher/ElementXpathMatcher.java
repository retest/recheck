package de.retest.recheck.review.ignore.matcher;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementXpathMatcher implements Matcher<Element> {

	private final String xpath;

	public ElementXpathMatcher( final Element element ) {
		this( element.getIdentifyingAttributes().getPath() );
	}

	private ElementXpathMatcher( final String xpath ) {
		this.xpath = xpath;
	}

	@Override
	public boolean test( final Element element ) {
		return element.getIdentifyingAttributes().getPath().matches( xpath );
	}

	@Override
	public String toString() {
		return String.format( ElementXpathMatcherLoader.FORMAT, xpath );
	}

	public static final class ElementXpathMatcherLoader extends RegexLoader<ElementXpathMatcher> {

		private static final String XPATH = "xpath=";

		private static final String FORMAT = XPATH + "%s";
		private static final Pattern REGEX = Pattern.compile( XPATH + "(.+)" );

		public ElementXpathMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected ElementXpathMatcher load( final MatchResult matcher ) {
			final String xpath = matcher.group( 1 );
			return new ElementXpathMatcher( xpath );
		}

		@Override
		public String save( final ElementXpathMatcher ignore ) {
			return ignore.toString();
		}
	}
}
