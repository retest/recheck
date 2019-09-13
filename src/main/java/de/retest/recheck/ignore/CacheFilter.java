package de.retest.recheck.ignore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CacheFilter implements Filter {

	private final Map<Element, Boolean> elementCache = new HashMap<>();
	private final Map<Pair<Element, AttributeDifference>, Boolean> attributeCache = new HashMap<>();
	private final Map<Pair<Element, String>, Boolean> attributeKeyCache = new HashMap<>();

	@Getter
	private final Filter base;

	@Override
	public boolean matches( final Element element ) {
		return elementCache.computeIfAbsent( element, matches() );
	}

	private Function<Element, Boolean> matches() {
		return base::matches;
	}

	@Override
	public boolean matches( final Element element, final ChangeType changeType ) {
		return elementCache.computeIfAbsent( element, e -> base.matches( e, changeType ) );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return attributeCache.computeIfAbsent( Pair.of( element, attributeDifference ), matchesDiff() );
	}

	private Function<Pair<Element, AttributeDifference>, Boolean> matchesDiff() {
		return p -> base.matches( p.getLeft(), p.getRight() );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return attributeKeyCache.computeIfAbsent( Pair.of( element, attributeKey ), matchesKeyDiff() );
	}

	private Function<Pair<Element, String>, Boolean> matchesKeyDiff() {
		return p -> base.matches( p.getLeft(), p.getRight() );
	}

	public static class FilterLoader implements Loader<CacheFilter> {

		@Override
		public Optional<CacheFilter> load( final String line ) {
			return Optional.empty();
		}

		@Override
		public String save( final CacheFilter ignore ) {
			return Loaders.filter().save( ignore.base );
		}
	}
}
