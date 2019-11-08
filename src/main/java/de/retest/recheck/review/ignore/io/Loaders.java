package de.retest.recheck.review.ignore.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.ignore.CacheFilter;
import de.retest.recheck.ignore.JSFilterImpl;
import de.retest.recheck.review.ignore.AttributeFilter;
import de.retest.recheck.review.ignore.AttributeFilter.AttributeFilterLoader;
import de.retest.recheck.review.ignore.AttributeRegexFilter;
import de.retest.recheck.review.ignore.AttributeRegexFilter.AttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.ElementAttributeFilter;
import de.retest.recheck.review.ignore.ElementAttributeFilter.ElementAttributeFilterLoader;
import de.retest.recheck.review.ignore.ElementAttributeRegexFilter;
import de.retest.recheck.review.ignore.ElementAttributeRegexFilter.ElementAttributeRegexFilterLoader;
import de.retest.recheck.review.ignore.ElementFilter;
import de.retest.recheck.review.ignore.ElementFilter.ElementFilterLoader;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;
import de.retest.recheck.review.ignore.JSFilterLoader;
import de.retest.recheck.review.ignore.PixelDiffFilter;
import de.retest.recheck.review.ignore.PixelDiffFilter.PixelDiffFilterLoader;
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

public class Loaders {

	private static final List<Pair<Class<?>, Loader<?>>> registeredLoaders = registerLoaders();

	private Loaders() {}

	private static List<Pair<Class<?>, Loader<?>>> registerLoaders() {
		final List<Pair<Class<?>, Loader<?>>> pairs = new ArrayList<>();
		pairs.add( Pair.of( ElementClassMatcher.class, new ElementClassMatcherLoader() ) );
		pairs.add( Pair.of( ElementIdMatcher.class, new ElementIdMatcherLoader() ) );
		pairs.add( Pair.of( ElementRetestIdMatcher.class, new ElementRetestIdMatcherLoader() ) );
		pairs.add( Pair.of( ElementXPathMatcher.class, new ElementXpathMatcherLoader() ) );
		pairs.add( Pair.of( ElementTypeMatcher.class, new ElementTypeMatcherLoader() ) );
		pairs.add( Pair.of( ElementAttributeFilter.class, new ElementAttributeFilterLoader() ) );
		pairs.add( Pair.of( ElementAttributeRegexFilter.class, new ElementAttributeRegexFilterLoader() ) );
		pairs.add( Pair.of( AttributeFilter.class, new AttributeFilterLoader() ) );
		pairs.add( Pair.of( AttributeRegexFilter.class, new AttributeRegexFilterLoader() ) );
		pairs.add( Pair.of( ElementFilter.class, new ElementFilterLoader() ) );
		pairs.add( Pair.of( PixelDiffFilter.class, new PixelDiffFilterLoader() ) );
		pairs.add( Pair.of( FilterPreserveLine.class, new FilterPreserveLineLoader() ) );
		pairs.add( Pair.of( CacheFilter.class, new CacheFilter.FilterLoader() ) );
		pairs.add( Pair.of( JSFilterImpl.class, new JSFilterLoader() ) );

		// This is error handling and should always be last
		pairs.add( Pair.of( FilterPreserveLine.class, new ErrorHandlingLoader() ) );
		return pairs;
	}

	public static <T> Loader<T> get( final Class<? extends T> clazz ) {
		return (Loader<T>) registeredLoaders.stream() //
				.filter( pair -> clazz.isAssignableFrom( pair.getLeft() ) ) //
				.findFirst() //
				.map( Pair::getRight ) //
				.orElseThrow( () -> new UnsupportedOperationException( "No loader registered for " + clazz ) );
	}

	public static Stream<String> save( final Stream<?> objects ) {
		return objects.map( Loaders::save );
	}

	public static <T> String save( final T object ) {
		final Loader<T> loader = (Loader<T>) get( object.getClass() );
		return loader.save( object );
	}

	public static Stream<?> load( final Stream<String> lines ) {
		return lines.map( Loaders::load ).filter( Objects::nonNull );
	}

	public static <T> T load( final String line ) {
		return (T) registeredLoaders.stream() //
				.map( Pair::getRight ) //
				.map( pair -> pair.load( line ) ) //
				.filter( Optional::isPresent ) //
				.parallel() //
				.findFirst() //
				.map( Optional::get ) //
				.orElse( null );
	}
}
