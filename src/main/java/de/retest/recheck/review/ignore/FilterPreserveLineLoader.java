package de.retest.recheck.review.ignore;

import org.apache.commons.lang3.StringUtils;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.review.ignore.FilterPreserveLineLoader.ShouldIgnorePreserveLine;
import de.retest.recheck.review.ignore.io.Loader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterPreserveLineLoader implements Loader<ShouldIgnorePreserveLine> {

	public static class ShouldIgnorePreserveLine implements Filter {

		public static final String COMMENT = "#";

		private final String line;

		public ShouldIgnorePreserveLine( final String line ) {
			this.line = line;
		}

		@Override
		public boolean matches( final Element element ) {
			return false;
		}

		@Override
		public boolean matches( final Element element,
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
		final boolean comment = line.startsWith( ShouldIgnorePreserveLine.COMMENT );
		if ( comment ) {
			return true;
		}

		final boolean onlyWhiteSpace = StringUtils.isWhitespace( line );
		if ( onlyWhiteSpace ) {
			return true;
		}

		final boolean leadingWhitespace = Character.isWhitespace( line.charAt( 0 ) );
		if ( leadingWhitespace ) {
			log.warn( "Please remove leading whitespace from the following line:\n{}", line );
			return true;
		}

		return false;
	}

	@Override
	public ShouldIgnorePreserveLine load( final String line ) {
		return new ShouldIgnorePreserveLine( line );
	}

	@Override
	public String save( final ShouldIgnorePreserveLine ignore ) {
		return ignore.toString();
	}

}
