package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class AttributeRegexFilter implements Filter {

	private final Pattern attributePattern;

	public AttributeRegexFilter( final String attributeRegex ) {
		attributePattern = Pattern.compile( attributeRegex );
	}

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return attributePattern.matcher( attributeDifference.getKey() ).matches();
	}

	@Override
	public String toString() {
		return String.format( AttributeRegexFilterLoader.FORMAT, attributePattern.toString() );
	}

	public static class AttributeRegexFilterLoader extends RegexLoader<AttributeRegexFilter> {

		private static final String KEY = "attribute-regex=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		public AttributeRegexFilterLoader() {
			super( REGEX );
		}

		@Override
		protected AttributeRegexFilter load( final MatchResult regex ) {
			final String attributeRegex = regex.group( 1 );
			return new AttributeRegexFilter( attributeRegex );
		}
	}
}
