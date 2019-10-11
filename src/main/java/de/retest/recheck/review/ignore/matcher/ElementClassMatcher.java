package de.retest.recheck.review.ignore.matcher;

import static java.util.stream.Collectors.toCollection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementClassMatcher implements Matcher<Element> {

	public static final String CLASS_KEY = "class";

	private final Set<String> classValues;

	public ElementClassMatcher( final Element element ) {
		this( toClassValuesSet( toClassValueString( element ) ) );
	}

	private ElementClassMatcher( final Set<String> classValues ) {
		this.classValues = classValues;
	}

	@Override
	public boolean test( final Element element ) {
		final Set<String> classValues = toClassValuesSet( toClassValueString( element ) );
		if ( classValues.isEmpty() ) {
			return false;
		}
		return this.classValues.containsAll( classValues );
	}

	@Override
	public String toString() {
		return String.format( ElementClassMatcherLoader.FORMAT, classValues );
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
			return new ElementClassMatcher( toClassValuesSet( classValue ) );
		}
	}

	private static String toClassValueString( final Element element ) {
		return element.getIdentifyingAttributes().get( CLASS_KEY );
	}

	private static Set<String> toClassValuesSet( final String classValue ) {
		if ( classValue == null ) {
			return Collections.emptySet();
		}
		return Stream.of( classValue.split( " " ) ).collect( toCollection( HashSet::new ) );
	}
}
