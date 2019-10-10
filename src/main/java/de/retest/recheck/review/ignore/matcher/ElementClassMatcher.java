package de.retest.recheck.review.ignore.matcher;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementClassMatcher implements Matcher<Element> {

	public static final String CLASS_KEY = "class";

	private final String classValue;

	public ElementClassMatcher( final Element element ) {
		this( Objects.toString( element.getIdentifyingAttributes().get( CLASS_KEY ), "" ) );
	}

	private ElementClassMatcher( final String classValue ) {
		this.classValue = classValue;
	}

	@Override
	public boolean test( final Element element ) {
		final String classValue = element.getIdentifyingAttributes().get( CLASS_KEY );
		return classValue != null && classValue.matches( this.classValue );
	}

	@Override
	public String toString() {
		return String.format( ElementClassMatcherLoader.FORMAT, classValue );
	}

	public static final class ElementClassMatcherLoader extends RegexLoader<ElementClassMatcher> {

		private static final String CLASS = CLASS_KEY + "=";

		private static final String FORMAT = CLASS + "%s";
		private static final Pattern REGEX = Pattern.compile( CLASS + "(.+)" );

		public ElementClassMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected ElementClassMatcher load( final MatchResult matcher ) {
			final String classValue = matcher.group( 1 );
			return new ElementClassMatcher( classValue );
		}
	}
}
