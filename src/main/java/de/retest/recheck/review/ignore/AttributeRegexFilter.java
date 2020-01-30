package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;

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
	public boolean matches( final Element element, final String attributeKey ) {
		return attributePattern.matcher( attributeKey ).matches();
	}

	@Override
	public String toString() {
		return String.format( AttributeRegexFilterLoader.FORMAT, attributePattern.toString() );
	}

	public static class AttributeRegexFilterLoader extends RegexLoader<Filter> {

		static final String KEY = "attribute-regex=";

		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		public AttributeRegexFilterLoader() {
			super( REGEX );
		}

		// Only needed for LegacyAttributeRegexFilterLoader
		protected AttributeRegexFilterLoader( final Pattern regex ) {
			super( regex );
		}

		@Override
		public String save( final Filter ignore ) {
			if ( ignore instanceof AllMatchFilter ) {
				return new AllMatchFilterLoader().save( (AllMatchFilter) ignore );
			}
			return super.save( ignore );
		}

		@Override
		protected Optional<Filter> load( final MatchResult regex ) {
			String match = regex.group( 1 );
			if ( !match.contains( "," ) ) {
				return Optional.of( new AttributeRegexFilter( match ) );
			}
			final String remainder = match.substring( match.indexOf( ',' ) + 1 ).trim();
			match = match.substring( 0, match.indexOf( ',' ) );
			// TODO Either no optional as return or no exception below
			final Filter chained = AttributeFilterLoader.chainableFilter.load( remainder ). //
					orElseThrow( () -> new IllegalArgumentException(
							"Couldn't find a filter for the expression '" + remainder + "'." ) );
			return Optional.of( new AllMatchFilter( new AttributeRegexFilter( match ), chained ) );
		}
	}

	// TODO Remove again after it was sufficiently long in the project
	// for all .filter and recheck.ignore files to be migrated
	public static class LegacyAttributeRegexFilterLoader extends AttributeRegexFilterLoader {
		public LegacyAttributeRegexFilterLoader() {
			super( Pattern.compile( "attribute-regex: (.+)" ) );
		}
	}
}
