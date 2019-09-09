package de.retest.recheck.persistence;

public class ExplicitMutableNamingStrategy implements NamingStrategy {

	private String suiteName;
	private String testName;

	/**
	 * Determines the suite name.
	 *
	 * @return name of the current test suite
	 */
	@Override
	public String getSuiteName() {
		return suiteName;
	}

	/**
	 * Setter to allow changing the name of the current suite. Please note that the caller must ensure thread safety
	 * when executing tests in parallel.
	 *
	 * @param suiteName
	 *            The name of the executing suite.
	 */
	public void setSuiteName( final String suiteName ) {
		this.suiteName = suiteName;
	}

	/**
	 * Determines the test name.
	 *
	 * @return name of the current test
	 */
	@Override
	public String getTestName() {
		return testName;
	}

	/**
	 * Setter to allow changing the name of the current test. Please note that the caller must ensure thread safety when
	 * executing tests in parallel.
	 *
	 * @param testName
	 *            The name of the executing test.
	 */
	public void setTestName( final String testName ) {
		this.testName = testName;
	}
}
