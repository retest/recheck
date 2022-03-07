package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.ignore.Filter;
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
			return ChainableFilterLoaderUtil.load( regex, AttributeRegexFilter::new );
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
