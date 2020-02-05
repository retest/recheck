package de.retest.recheck.review.ignore.matcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementClassMatcher implements Matcher<Element> {

	public static final String CLASS_KEY = "class";

	private final List<String> classes;

	public ElementClassMatcher( final Element element ) {
		this( toClassValuesList( toClassValueString( element ) ) );
	}

	private ElementClassMatcher( final List<String> classValues ) {
		classes = classValues;
	}

	@Override
	public boolean test( final Element element ) {
		final List<String> elementClasses = toClassValuesList( toClassValueString( element ) );
		if ( classes.isEmpty() ) {
			return false;
		}
		return elementClasses.containsAll( classes );
	}

	@Override
	public String toString() {
		final String classValueString = classes.stream().collect( Collectors.joining( " " ) );
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
		protected Optional<ElementClassMatcher> load( final MatchResult matcher ) {
			final String classValue = matcher.group( 1 );
			return Optional.of( new ElementClassMatcher( toClassValuesList( classValue ) ) );
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
