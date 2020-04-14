package de.retest.recheck.review.ignore.matcher;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementByAttributeMatcher implements Matcher<Element> {

	private final String key;
	private final String value;

	public ElementByAttributeMatcher( final String key, final String value ) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean test( final Element element ) {
		Object elementAttribute = element.getIdentifyingAttributes().get( key );
		if ( elementAttribute != null ) {
			return value.equals( elementAttribute );
		}
		elementAttribute = element.getAttributes().get( key );
		return value.equals( elementAttribute );
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	public static final class ElementByAttributeMatcherLoader extends RegexLoader<ElementByAttributeMatcher> {

		private static final Pattern REGEX = Pattern.compile( "(.+)=(.+)" );

		public ElementByAttributeMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementByAttributeMatcher> load( final MatchResult matcher ) {
			final String key = matcher.group( 1 );
			final String value = matcher.group( 2 );
			return Optional.of( new ElementByAttributeMatcher( key, value ) );
		}
	}
}
