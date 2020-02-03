package de.retest.recheck.review.ignore;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;
import de.retest.recheck.review.ignore.AttributeFilter.LegacyAttributeFilterLoader;
import de.retest.recheck.review.ignore.AttributeRegexFilter.AttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.AttributeRegexFilter.LegacyAttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.io.InheritanceLoader;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;

public class MatcherFilter implements Filter {

	private final Matcher<Element> matcher;

	public MatcherFilter( final Matcher<Element> matcher ) {
		this.matcher = matcher;
	}

	@Override
	public boolean matches( final Element element ) {
		if ( matcher.test( element ) ) {
			return true;
		}
		final Element parent = element.getParent();
		return parent != null && matches( parent );
	}

	@Override
	public String toString() {
		return MatcherFilterLoader.MATCHER + matcher.toString();
	}

	public static class MatcherFilterLoader extends RegexLoader<Filter> {

		private static final Loader<Filter> chainableFilter = new InheritanceLoader<>( Arrays.asList( //
				Pair.of( AttributeFilter.class, new AttributeFilterLoader() ), //
				Pair.of( AttributeFilter.class, new LegacyAttributeFilterLoader() ), //
				Pair.of( AttributeRegexFilter.class, new AttributeRegexFilterLoader() ), //
				Pair.of( AttributeRegexFilter.class, new LegacyAttributeRegexFilterLoader() ) //
		) );

		static final String MATCHER = "matcher: ";

		private static final Pattern REGEX = Pattern.compile( MATCHER + "(.+)" );

		public MatcherFilterLoader() {
			super( REGEX );
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
			// TODO Either no optional as return or no exception below
			return ChainableFilterLoaderUtil.load( regex,
					match -> new MatcherFilter( Loaders.elementMatcher().load( match )
							.orElseThrow( () -> new IllegalArgumentException(
									"Couldn't find a filter for the expression '" + match + "'." ) ) ),
					chainableFilter );
		}
	}
}
