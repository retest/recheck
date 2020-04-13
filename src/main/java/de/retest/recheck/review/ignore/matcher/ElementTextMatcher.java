package de.retest.recheck.review.ignore.matcher;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementTextMatcher implements Matcher<Element> {

	public static final String TEXT_KEY = "text";

	private final String givenText;
	private final String normalizedText;

	public ElementTextMatcher( final Element element ) {
		normalizedText = normalize( element.getIdentifyingAttributes().get( TEXT_KEY ) );
		givenText = normalizedText;
	}

	private ElementTextMatcher( final String text ) {
		givenText = text;
		normalizedText = normalize( text );
	}

	private String normalize( final String text ) {
		// TODO normalize special characters...
		return text.trim();
	}

	@Override
	public boolean test( final Element element ) {
		return normalize( element.getIdentifyingAttributes().get( TEXT_KEY ) ).equals( normalizedText );
	}

	@Override
	public String toString() {
		return String.format( ElementTextMatcherLoader.FORMAT, givenText );
	}

	public static final class ElementTextMatcherLoader extends RegexLoader<ElementTextMatcher> {

		private static final String TEXT = TEXT_KEY + "=";

		private static final String FORMAT = TEXT + "%s";
		private static final Pattern REGEX = Pattern.compile( TEXT + "(.+)" );

		public ElementTextMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementTextMatcher> load( final MatchResult matcher ) {
			final String text = matcher.group( 1 );
			return Optional.of( new ElementTextMatcher( text ) );
		}
	}
}
