package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

	public static class FilterLoader extends RegexLoader<ExcludeFilter> {

		private static final String FORMAT = "exclude(%s)";

		private final Loader<Filter> delegate;

		public FilterLoader( final Loader<Filter> delegate ) {
			super( Pattern.compile( "exclude\\((.+)\\)" ) );
			this.delegate = delegate;
		}

		@Override
		protected Optional<ExcludeFilter> load( final MatchResult matcher ) {
			return delegate.load( matcher.group( 1 ) ) //
					.map( ExcludeFilter::new );
		}

		@Override
		public String save( final ExcludeFilter filter ) {
			return format( filter, delegate::save );
		}

		private static String format( final ExcludeFilter filter, final Function<Filter, String> wrapped ) {
			return String.format( FORMAT, wrapped.apply( filter.filter ) );
		}
	}
}
