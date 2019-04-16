package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class AttributeFilter implements Filter {

	private final String attribute;

	public AttributeFilter( final String attribute ) {
		this.attribute = attribute;
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element,
			final AttributeDifference attributeDifference ) {
		return attributeDifference.getKey().equals( attribute );
	}

	@Override
	public String toString() {
		return String.format( AttributeFilterLoader.FORMAT, attribute );
	}

	public static class AttributeFilterLoader extends RegexLoader<AttributeFilter> {

		private static final String KEY = "attribute=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		public AttributeFilterLoader() {
			super( REGEX );
		}

		@Override
		protected AttributeFilter load( final MatchResult regex ) {
			final String attribute = regex.group( 1 );
			return new AttributeFilter( attribute );
		}
	}
}
