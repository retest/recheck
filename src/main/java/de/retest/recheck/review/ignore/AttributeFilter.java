package de.retest.recheck.review.ignore;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeRegexFilter.AttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;
import de.retest.recheck.review.ignore.io.InheritanceLoader;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import lombok.extern.slf4j.Slf4j;

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
	public boolean matches( final Element element, final String attributeKey ) {
		return attributeKey.equals( attribute );
	}

	@Override
	public String toString() {
		return String.format( AttributeFilterLoader.FORMAT, attribute );
	}

	@Slf4j
	public static class AttributeFilterLoader extends RegexLoader<Filter> {

		public static final Loader<Filter> chainableFilter = new InheritanceLoader<>( Arrays.asList( //
				Pair.of( PixelDiffFilter.class, new PixelDiffFilterLoader() ) //
		) );

		private static final String KEY = "attribute=";
		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		private static final String POSSIBLE_REGEX = "*";

		public AttributeFilterLoader() {
			super( REGEX );
		}

		// Only needed for LegacyAttributeFilterLoader
		protected AttributeFilterLoader( final Pattern regex ) {
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
				return Optional.of( loadSimpleAttributeFilter( match ) );
			}
			final String remainder = match.substring( match.indexOf( ',' ) + 1 ).trim();
			match = match.substring( 0, match.indexOf( ',' ) );
			// TODO Either no optional as return or no exception below
			final Filter chained = chainableFilter.load( remainder ). //
					orElseThrow( () -> new IllegalArgumentException(
							"Couldn't find a filter for the expression '" + remainder + "'." ) );
			return Optional.of( new AllMatchFilter( loadSimpleAttributeFilter( match ), chained ) );
		}

		private AttributeFilter loadSimpleAttributeFilter( final String attribute ) {
			if ( attribute.contains( POSSIBLE_REGEX ) ) {
				final String actualLine = KEY + attribute;
				final String suggestedLine = AttributeRegexFilterLoader.KEY + attribute;
				log.warn( "'{}' contains '{}'. For regular expressions, please use '{}'.", actualLine, POSSIBLE_REGEX,
						suggestedLine );
			}
			return new AttributeFilter( attribute.trim() );
		}
	}

	// TODO Remove again after it was sufficiently long in the project
	// for all .filter and recheck.ignore files to be migrated
	public static class LegacyAttributeFilterLoader extends AttributeFilterLoader {
		public LegacyAttributeFilterLoader() {
			super( Pattern.compile( "attribute: (.+)" ) );
		}
	}
}
