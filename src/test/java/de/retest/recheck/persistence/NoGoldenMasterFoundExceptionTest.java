package de.retest.recheck.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class NoGoldenMasterFoundExceptionTest {

	@Test
	void should_fail_if_no_files_provided() throws Exception {
		assertThatThrownBy( NoGoldenMasterFoundException::new ) //
				.isInstanceOf( IllegalArgumentException.class )
				.hasMessage( "You should at least provide one Golden Master file." );
	}

	@Test
	void should_format_single_golden_master_accordingly() throws Exception {
		final NoGoldenMasterFoundException cut = new NoGoldenMasterFoundException( "foo" );

		assertThat( cut ).hasMessage( "The following Golden Master(s) cannot be found:\n" //
				+ "\t- foo" );
	}

	@Test
	void should_format_multiple_golden_masters_accordingly() throws Exception {
		final NoGoldenMasterFoundException cut = new NoGoldenMasterFoundException( "foo", "bar" );

		assertThat( cut ).hasMessage( "The following Golden Master(s) cannot be found:\n" //
				+ "\t- foo\n" //
				+ "\t- bar" );
	}

}
