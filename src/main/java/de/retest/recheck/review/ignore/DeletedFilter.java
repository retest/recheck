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
public class DeletedFilter implements Filter {

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final ChangeType changeType ) {
		return changeType == ChangeType.Deleted;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return false;
	}

	@Override
	public String toString() {
		return DeletedFilterLoader.KEY;
	}

	public static class DeletedFilterLoader extends RegexLoader<DeletedFilter> {

		private static final String KEY = "deleted";
		private static final Pattern REGEX = Pattern.compile( KEY );

		public DeletedFilterLoader() {
			super( REGEX );
		}

		@Override
		protected Optional<DeletedFilter> load( final MatchResult regex ) {
			return Optional.of( new DeletedFilter() );
		}
	}
}
