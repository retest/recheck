package de.retest.recheck.ignore;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.Loaders;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;
import lombok.ToString;

/**
 * Combines multiple filters into one using {@link Stream#allMatch(java.util.function.Predicate)}. This means that this
 * filter only returns true if all of its sub-filters return true ... essentially combining the filters via an AND.
 */
@ToString
public class AllMatchFilter implements Filter {

	@Getter
	private final List<Filter> filters;

	public AllMatchFilter( final Filter... filters ) {
		this( Arrays.asList( filters ) );
	}

	public AllMatchFilter( final List<Filter> filters ) {
		this.filters = filters;
		if ( filters.isEmpty() ) {
			throw new IllegalArgumentException(
					"Given filters must not be empty, please use Filter.ALWAYS_MATCH or Filter.NEVER_MATCH." );
		}
	}

	@Override
	public boolean matches( final Element element ) {
		return filters.stream().allMatch( f -> f.matches( element ) );
	}

	@Override
	public boolean matches( final Element element, final ChangeType change ) {
		return filters.stream().allMatch( f -> f.matches( element, change ) );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return filters.stream().allMatch( f -> f.matches( element, attributeDifference ) );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return filters.stream().allMatch( f -> f.matches( element, attributeKey ) );
	}

	public static class AllMatchFilterLoader implements Loader<AllMatchFilter> {

		@Override
		public Optional<AllMatchFilter> load( final String line ) {
			return Optional.empty();
		}

		@Override
		public String save( final AllMatchFilter ignore ) {
			return ignore.filters.stream().map( f -> Loaders.filter().save( f ) ).collect( Collectors.joining( ", " ) );
		}

	}
}
