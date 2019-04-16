package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import de.retest.recheck.review.ignore.FilterPreserveLineLoader.ShouldIgnorePreserveLine;
import de.retest.recheck.review.ignore.io.Loader;

public class FilterPreserveLineTest {

	@Rule
	public final SystemOutRule systemOut = new SystemOutRule().enableLog();

	@Test
	public void should_load_comments() throws Exception {
		final String comment = ShouldIgnorePreserveLine.COMMENT + " some comment";
		final Loader<ShouldIgnorePreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.canLoad( comment ) ).isTrue();
	}

	@Test
	public void should_load_only_whitespace() throws Exception {
		final String whitespace0 = "";
		final String whitespace1 = " ";
		final String whitespace2 = "\n";
		final String whitespace3 = "\t";
		final Loader<ShouldIgnorePreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.canLoad( whitespace0 ) ).isTrue();
		assertThat( cut.canLoad( whitespace1 ) ).isTrue();
		assertThat( cut.canLoad( whitespace2 ) ).isTrue();
		assertThat( cut.canLoad( whitespace3 ) ).isTrue();
	}

	@Test
	public void should_load_leading_whitespace_and_warn() throws Exception {
		final String line = " foo bar baz";
		final Loader<ShouldIgnorePreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.canLoad( line ) ).isTrue();
		assertThat( systemOut.getLog() )
				.contains( "Please remove leading whitespace from the following line:\n" + line );
	}

}
