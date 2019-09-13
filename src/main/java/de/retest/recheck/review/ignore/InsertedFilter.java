package de.retest.recheck.review.ignore;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.Getter;

@Getter
public class InsertedFilter implements Filter {

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final ChangeType changeType ) {
		return changeType == ChangeType.Inserted;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return false;
	}

	@Override
	public String toString() {
		return InsertedFilterLoader.KEY;
	}

	public static class InsertedFilterLoader extends RegexLoader<InsertedFilter> {

		private static final String KEY = "inserted";
		private static final Pattern REGEX = Pattern.compile( KEY );

		public InsertedFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<InsertedFilter> load( final MatchResult regex ) {
			return Optional.of( new InsertedFilter() );
		}
	}
}
