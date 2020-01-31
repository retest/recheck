package de.retest.recheck.review.ignore.io;

import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ignore.AllMatchFilter;
import de.retest.recheck.ignore.AllMatchFilter.AllMatchFilterLoader;
import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;
import de.retest.recheck.review.ignore.AttributeRegexFilter;
import de.retest.recheck.review.ignore.AttributeRegexFilter.AttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.ColorDiffFilter;
import de.retest.recheck.review.ignore.ColorDiffFilter.ColorDiffFilterLoader;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;
import de.retest.recheck.review.ignore.ImportedExternalFilter;
import de.retest.recheck.review.ignore.JSFilterLoader;
import de.retest.recheck.review.ignore.MatcherFilter;
import de.retest.recheck.review.ignore.MatcherFilter.MatcherFilterLoader;
import de.retest.recheck.review.ignore.PixelDiffFilter;
import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;
import de.retest.recheck.review.ignore.ValueRegexFilter;
import de.retest.recheck.review.ignore.ValueRegexFilter.ValueRegexFilterLoader;
import de.retest.recheck.review.ignore.matcher.ElementClassMatcher;
import de.retest.recheck.review.ignore.matcher.ElementClassMatcher.ElementClassMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher.ElementIdMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher.ElementRetestIdMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementTypeMatcher;
import de.retest.recheck.review.ignore.matcher.ElementTypeMatcher.ElementTypeMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher.ElementXpathMatcherLoader;
import de.retest.recheck.review.ignore.matcher.Matcher;
import de.retest.recheck.ui.descriptors.Element;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor( access = AccessLevel.PRIVATE )
public class Loaders {

	private static final Loader<Filter> filter = new InheritanceLoader<>( Arrays.asList( //
			Pair.of( MatcherFilter.class, new MatcherFilterLoader() ), //
			Pair.of( AttributeFilter.class, new AttributeFilterLoader() ), //
			Pair.of( AttributeRegexFilter.class, new AttributeRegexFilterLoader() ), //
			Pair.of( PixelDiffFilter.class, new PixelDiffFilterLoader() ), //
			Pair.of( ValueRegexFilter.class, new ValueRegexFilterLoader() ), //
			Pair.of( ColorDiffFilter.class, new ColorDiffFilterLoader() ), //
			Pair.of( FilterPreserveLine.class, new FilterPreserveLineLoader() ), //
			Pair.of( ImportedExternalFilter.class, new ImportExternalFilterLoader() ), //
			Pair.of( AllMatchFilter.class, new AllMatchFilterLoader() ), //
			Pair.of( CacheFilter.class, new CacheFilter.FilterLoader() ), //
			Pair.of( JSFilterImpl.class, new JSFilterLoader() ), //

			// This is error handling and should always be last
			Pair.of( Filter.class, new ErrorHandlingLoader() ) //
	) );

	public static Loader<Filter> filter() {
		return Loaders.filter;
	}

	private static final Loader<Matcher<Element>> elementMatcher = new InheritanceLoader<>( Arrays.asList( //
			Pair.of( ElementClassMatcher.class, new ElementClassMatcherLoader() ), //
			Pair.of( ElementIdMatcher.class, new ElementIdMatcherLoader() ), //
			Pair.of( ElementRetestIdMatcher.class, new ElementRetestIdMatcherLoader() ), //
			Pair.of( ElementXPathMatcher.class, new ElementXpathMatcherLoader() ), //
			Pair.of( ElementTypeMatcher.class, new ElementTypeMatcherLoader() ) //
	) );

	public static Loader<Matcher<Element>> elementMatcher() {
		return Loaders.elementMatcher;
	}
}
