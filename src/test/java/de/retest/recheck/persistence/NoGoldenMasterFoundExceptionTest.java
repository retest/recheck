package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoGoldenMasterFoundExceptionTest {

	@Test
	void should_format_filenames() throws Exception {
		final NoGoldenMasterFoundException cut0 = new NoGoldenMasterFoundException( "foo" );
		assertThat( cut0 ).hasMessageEndingWith( "\tfoo" );

		final NoGoldenMasterFoundException cut1 = new NoGoldenMasterFoundException( "foo", "bar" );
		assertThat( cut1 ).hasMessageEndingWith( "\tfoo\n\tbar" );
	}

}
