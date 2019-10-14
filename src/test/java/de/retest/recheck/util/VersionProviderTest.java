package de.retest.recheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class VersionProviderTest {

	@Test
	public void retest_version_is_not_null() {
		assertThat( VersionProvider.RETEST_VERSION ).isNotNull();
	}

}
