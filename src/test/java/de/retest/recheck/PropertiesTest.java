package de.retest.recheck;

import org.junit.jupiter.api.Test;

class PropertiesTest {

	@Test
	public void getReTestInstallDir_should_be_robust() throws Exception {
		// no crash
		Properties.getReTestInstallDir();
	}

}
