package de.retest.recheck.review.ignore;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.MatchResult;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;
import de.retest.recheck.review.ignore.ValueRegexFilter.ValueRegexFilterLoader;
import de.retest.recheck.review.ignore.io.InheritanceLoader;
import de.retest.recheck.review.ignore.io.Loader;

class ChainableFilterLoaderUtil {

	private static final Loader<Filter> chainableFilter = new InheritanceLoader<>( Arrays.asList( //
			Pair.of( PixelDiffFilter.class, new PixelDiffFilterLoader() ), //
			Pair.of( ValueRegexFilter.class, new ValueRegexFilterLoader() ) //
	) );

	public static Optional<Filter> load( final MatchResult regex, final Function<String, Filter> simpleFilterLoader ) {
		String match = regex.group( 1 );
		if ( !match.contains( "," ) ) {
			return Optional.of( simpleFilterLoader.apply( match ) );
		}
		final String remainder = match.substring( match.indexOf( ',' ) + 1 ).trim();
		match = match.substring( 0, match.indexOf( ',' ) );
		// TODO Either no optional as return or no exception below
		final Filter chained = chainableFilter.load( remainder ). //
				orElseThrow( () -> new IllegalArgumentException(
						"Couldn't find a filter for the expression '" + remainder + "'." ) );
		return Optional.of( new AllMatchFilter( simpleFilterLoader.apply( match ), chained ) );
	}
}
