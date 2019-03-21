package de.retest.recheck.review.ignore;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.ShouldIgnoreEmptyLineLoader.ShouldIgnoreEmptyLine;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class ShouldIgnoreEmptyLineLoader implements Loader<ShouldIgnoreEmptyLine> {

	public static class ShouldIgnoreEmptyLine implements ShouldIgnore {

		private final String line;

		public ShouldIgnoreEmptyLine( final String line ) {
			this.line = line;
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

		@Override
		public String toString() {
			return line;
		}

	}

	@Override
	public boolean canLoad( final String line ) {
		return StringUtils.isEmpty( line );
	}

	@Override
	public ShouldIgnoreEmptyLine load( final String line ) {
		return new ShouldIgnoreEmptyLine( line );
	}

	@Override
	public String save( final ShouldIgnoreEmptyLine ignore ) {
		return ignore.line;
	}

}
