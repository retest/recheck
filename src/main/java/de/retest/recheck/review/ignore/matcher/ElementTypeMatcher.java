package de.retest.recheck.review.ignore.matcher;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

public class ElementTypeMatcher implements Matcher<Element> {

	private final String type;

	public ElementTypeMatcher( final Element element ) {
		this( element.getIdentifyingAttributes().getType() );
	}

	private ElementTypeMatcher( final String type ) {
		this.type = type;
		assert type != null;
	}

	@Override
	public boolean test( final Element element ) {
		return element.getIdentifyingAttributes().getType().matches( type );
	}

	@Override
	public String toString() {
		return String.format( ElementTypeMatcherLoader.FORMAT, type );
	}

	public static final class ElementTypeMatcherLoader extends RegexLoader<ElementTypeMatcher> {

		private static final String TYPE = "type=";

		private static final String FORMAT = TYPE + "%s";
		private static final Pattern REGEX = Pattern.compile( TYPE + "(.+)" );

		public ElementTypeMatcherLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<ElementTypeMatcher> load( final MatchResult matcher ) {
			final String type = matcher.group( 1 );
			return Optional.of( new ElementTypeMatcher( type ) );
		}
	}
}
