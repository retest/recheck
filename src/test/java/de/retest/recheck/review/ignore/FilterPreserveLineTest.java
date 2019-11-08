package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import de.retest.recheck.review.ignore.FilterPreserveLineLoader.FilterPreserveLine;
import de.retest.recheck.review.ignore.io.Loader;

public class FilterPreserveLineTest {

	@Rule
	public final SystemOutRule systemOut = new SystemOutRule().enableLog();

	@Test
	public void should_load_comments() throws Exception {
		final String comment = FilterPreserveLine.COMMENT + " some comment";
		final Loader<FilterPreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.load( comment ) ).isPresent();
	}

	@Test
	public void should_load_only_whitespace() throws Exception {
		final String whitespace0 = "";
		final String whitespace1 = " ";
		final String whitespace2 = "\n";
		final String whitespace3 = "\t";
		final Loader<FilterPreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.load( whitespace0 ) ).isPresent();
		assertThat( cut.load( whitespace1 ) ).isPresent();
		assertThat( cut.load( whitespace2 ) ).isPresent();
		assertThat( cut.load( whitespace3 ) ).isPresent();
	}

	@Test
	public void should_load_leading_whitespace_and_warn() throws Exception {
		final String line = " foo bar baz";
		final Loader<FilterPreserveLine> cut = new FilterPreserveLineLoader();
		assertThat( cut.load( line ) ).isPresent();
		assertThat( systemOut.getLog() )
				.contains( "Please remove leading whitespace from the following line:\n" + line );
	}

}
