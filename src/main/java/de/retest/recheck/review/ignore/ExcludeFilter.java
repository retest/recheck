package de.retest.recheck.review.ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.retest.recheck.ignore.CompoundFilter;
import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExcludeFilter implements Filter {

	@Getter( AccessLevel.PACKAGE )
	private final Filter filter;

	@Override
	public boolean matches( final Element element ) {
		return !filter.matches( element );
	}

	@Override
	public boolean matches( final Element element, final ChangeType change ) {
		return !filter.matches( element, change );
	}

	@Override
	public boolean matches( final Element element, final String attributeKey ) {
		return !filter.matches( element, attributeKey );
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return !filter.matches( element, attributeDifference );
	}

	@Override
	public String toString() {
		return String.format( FilterLoader.FORMAT, filter );
	}

	public static class FilterLoader implements Loader<ExcludeFilter> {

		private static final String FORMAT = "exclude(%s)";
		private static final Pattern controlRegex = Pattern.compile( "^exclude\\((.+?)\\)(?:, exclude\\((.+?)\\))*$" );
		// Excludes can be nested, but must then only take the outermost closing parenthesis.
		// Therefore a lookahead is necessary for the first and subsequent chunks, as well as the last chunk
		private static final Pattern chunkRegex = Pattern.compile(
				"(?:^exclude\\((.+?)\\)(?=, ))|(?:, exclude\\((.+?)\\)(?=, ))|(?:(?:, )?exclude\\((.+?)\\)$)" );

		private final Loader<Filter> delegate;

		public FilterLoader( final Loader<Filter> delegate ) {
			this.delegate = delegate;
		}

		@Override
		public Optional<ExcludeFilter> load( final String line ) {
			// Variable groups cannot be easily matched with Java Regex (see https://stackoverflow.com/a/6939587)
			final Matcher control = controlRegex.matcher( line );
			if ( !control.matches() ) {
				return Optional.empty();
			}
			final Matcher chunks = chunkRegex.matcher( line );
			final List<Filter> filters = new ArrayList<>();
			while ( chunks.find() ) {
				final String start = chunks.group( 1 );
				final String middle = chunks.group( 2 );
				final String end = chunks.group( 3 );
				// Only one group matches, find out which
				final String inner = start != null ? start : middle != null ? middle : end;
				final Optional<Filter> load = delegate.load( inner );
				if ( load.isPresent() ) {
					filters.add( load.get() );
				} else { // There was a loading error, abort
					log.warn( "Could not find loader for inner part '{}'.", inner );
					return Optional.empty();
				}
			}
			// Filters should never be empty at this point
			return Optional.of( new ExcludeFilter( new CompoundFilter( filters ) ) );
		}

		@Override
		public String save( final ExcludeFilter filter ) {
			final Filter inner = filter.getFilter();
			if ( inner instanceof CompoundFilter ) {
				final List<Filter> filters = ((CompoundFilter) inner).getFilters();
				return filters.stream() //
						.map( filter1 -> String.format( FORMAT, delegate.save( filter1 ) ) ) //
						.collect( Collectors.joining( ", " ) );
			}
			return String.format( FORMAT, delegate.save( inner ) );
		}
	}
}
