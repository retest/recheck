package de.retest.recheck.review.ignore.matcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementClassMatcher implements Matcher<Element> {

	public static final String CLASS_KEY = "class";

	private final List<String> classValues;

	public ElementClassMatcher( final Element element ) {
		this( toClassValuesList( toClassValueString( element ) ) );
	}

	private ElementClassMatcher( final List<String> classValues ) {
		this.classValues = classValues;
	}

	@Override
	public boolean test( final Element element ) {
		final List<String> classValues = toClassValuesList( toClassValueString( element ) );
		if ( classValues.isEmpty() ) {
			return false;
		}
		return this.classValues.containsAll( classValues );
	}

	@Override
	public String toString() {
		final String classValueString = classValues.stream().collect( Collectors.joining( " " ) );
		return String.format( ElementClassMatcherLoader.FORMAT, classValueString );
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
			return new ElementClassMatcher( toClassValuesList( classValue ) );
		}
	}

	private static String toClassValueString( final Element element ) {
		return element.getIdentifyingAttributes().get( CLASS_KEY );
	}

	private static List<String> toClassValuesList( final String classValue ) {
		if ( classValue == null ) {
			return Collections.emptyList();
		}
		return Arrays.asList( classValue.split( " " ) );
	}
}
