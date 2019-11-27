package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeRegexFilter.AttributeRegexFilterLoader;
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
	public static class AttributeFilterLoader extends RegexLoader<AttributeFilter> {

		static final String KEY = "attribute=";

		private static final String FORMAT = KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( KEY + "(.+)" );

		private static final String POSSIBLE_REGEX = "*";

		public AttributeFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<AttributeFilter> load( final MatchResult regex ) {
			final String attribute = regex.group( 1 );
			if ( attribute.contains( POSSIBLE_REGEX ) ) {
				final String actualLine = KEY + attribute;
				final String suggestedLine = AttributeRegexFilterLoader.KEY + attribute;
				log.warn( "'{}' contains '{}'. For regular expressions, please use '{}'.", actualLine, POSSIBLE_REGEX,
						suggestedLine );
			}
			return Optional.of( new AttributeFilter( attribute ) );
		}
	}
}
