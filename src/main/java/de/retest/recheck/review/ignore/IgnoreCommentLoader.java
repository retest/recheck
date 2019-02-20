package de.retest.recheck.review.ignore;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.IgnoreCommentLoader.ShouldIgnoreComment;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class IgnoreCommentLoader implements Loader<ShouldIgnoreComment> {

	public static class ShouldIgnoreComment implements ShouldIgnore {

		private final String comment;

		public ShouldIgnoreComment( final String comment ) {
			this.comment = comment;
		}

		@Override
		public boolean shouldIgnoreElement( final Element element ) {
			return false;
		}

		@Override
		public boolean shouldIgnoreAttributeDifference( final Element element,
				final AttributeDifference attributeDifference ) {
			return false;
		}

	}

	@Override
	public boolean canLoad( final String line ) {
		return line.trim().startsWith( "#" );
	}

	@Override
	public ShouldIgnoreComment load( final String line ) {
		return new ShouldIgnoreComment( line.replaceFirst( "#", "" ).trim() );
	}

	@Override
	public String save( final ShouldIgnoreComment ignore ) {
		return "# " + ignore.comment;
	}
}
